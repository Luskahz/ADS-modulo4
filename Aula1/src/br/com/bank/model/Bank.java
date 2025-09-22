/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.model;

import br.com.bank.dao.AccountDAO;
import br.com.bank.dao.BankDAO;
import br.com.bank.exceptions.InvalidInputException;
import java.sql.Connection;
import br.com.bank.dao.Conexao;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author lucas
 */
public final class Bank {

    //Attributes
    private final Map<Integer, Account> bank;
    private final Path filepath;
    private final Path statementPath;
    private final BankDAO bankDAO = new BankDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private int timeToAppliTaxinMills = 5000;
    
    //Constructor
    public Bank(Path path, Path statementPath) throws IOException {
        this.bank = new HashMap<>();
        this.filepath = path;
        this.statementPath = statementPath;
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        if (statementPath.getParent() != null) {
            Files.createDirectories(statementPath.getParent());
        }
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        if (!Files.exists(statementPath)) {
            Files.createFile(statementPath);
        }

        refreshAllAccountsWithFileorDb();

    }

    //Getters
    public int getSize() {
        return bank.size();
    }

    public int getTimeToAppliTaxinMills() {
        return timeToAppliTaxinMills;
    }
    
    public void removeAccount(int id) {
        bank.remove(id);
    }

    public double getSumBalances() {
        return bank.values().stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    public Path getStatementPath() {
        return statementPath;
    }

    public Account getAccountById(int accountId) {
        return bank.get(accountId); //retorna a referencia na memoria;
    }

    public Map<Integer, Account> getAllAccounts() {
        return Collections.unmodifiableMap(bank); // o cara que importar isso n vai poder alterar a poha do seu ponteiro 
    }

    //Setters
    public void addAccount(Account account) throws InvalidInputException {
        if (bank.containsKey(account.getId())) {
            throw new InvalidInputException("Já existe uma conta com esse ID.");
        }
        bank.put(account.getId(), account);
    }

    //Methods
    public int saveAllAccountsToDb() throws IOException{
        int updatesInAccounts = 0;
        try { // esse try deleta as contas, verifica se ela foi excluida durante a utulização do sistema, e se tiver sido, apaga ela do banco tbm
            Map<Integer, Account> dbAccounts = bankDAO.getAllAccounts();
            for (Map.Entry<Integer, Account> dbAccount : dbAccounts.entrySet()) {
                Account AccountTe = getAccountById(dbAccount.getValue().getId());
                if (AccountTe == null) {
                    accountDAO.deleteAccount(dbAccount.getValue());
                }
            }

            try (Connection conn = Conexao.getConnection()) {
                for (Map.Entry<Integer, Account> entry : bank.entrySet()) {
                    Account bankAccount = entry.getValue(); // conta que eexiste no bank
                    Account dbAccount = accountDAO.getAccount(bankAccount.getId()); // conta se existir no bd
                    if (dbAccount == null) {//se ele n achar a conta que está no bank no db
                        boolean inserted = accountDAO.insertAccount(bankAccount); // ele tenta inserir a conta no banco
                        if (inserted) {
                            continue;
                        }
                    }

                    if (!bankAccount.getHolder().equals(dbAccount.getHolder())) {
                        int success;
                        success = accountDAO.updateHolder(bankAccount.getId(), bankAccount.getHolder());
                        if (success > 0) {
                            updatesInAccounts++;
                        }
                    }

                    if (bankAccount.getBalance() != dbAccount.getBalance()) {
                        int success = accountDAO.updateBalance(bankAccount.getId(), bankAccount.getBalance());
                        if (success > 0) {
                            updatesInAccounts++;
                        }
                    }
                }
            }

        } catch (SQLNonTransientConnectionException e) {
            // Fallback → salvar no arquivo
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, Account> entry : bank.entrySet()) {
                Account account = entry.getValue();
                sb.append(account.getId()).append(";");
                sb.append(account.getHolder()).append(";");
                sb.append(account.getBalance()).append(";");
                sb.append(account.getTaxationStrategy().name()).append(";\n");
            }

            Files.writeString(this.filepath, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            JOptionPane.showMessageDialog(
                    null,
                    "Offline database, saving the accounts in the accounts.txt.",
                    "Database",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "error when you try to save the acccounts, try again later\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return updatesInAccounts;
    }

    public void refreshAllAccountsWithFileorDb() throws IOException {
        Map<Integer, Account> dbBank = bankDAO.getAllAccounts();

        if (dbBank.isEmpty()) {//falback
            Map<Integer, AccountCurrent> tmp = new HashMap<>();
            for (String line : Files.readAllLines(filepath)) {
                String[] parts = line.split(";");
                int id = Integer.parseInt(parts[0]);
                double balance = Double.parseDouble(parts[2]);

                AccountCurrent account = new AccountCurrent(id, parts[1], balance);
                tmp.put(id, account);
            }

            bank.clear();
            bank.putAll(tmp);

        } else {
            bank.clear();
            bank.putAll(dbBank);
        }

    }
    
    
    
    private void applyFee(){
        Map<Integer, Account> dbBank = bankDAO.getAllAccounts();
        Instant now = Instant.now();
        
        
        
      
      
    };

}
