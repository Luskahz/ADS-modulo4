/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.model;

import br.com.bank.dao.AccountCurrentDAO;
import br.com.bank.exceptions.InvalidInputException;
import java.sql.Connection;
import br.com.bank.dao.Conexao;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.sql.SQLException;
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
    private final Map<Integer, AccountCurrent> bank;
    private final Path filepath;
    private final Path statementPath;

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

        refreshAllAccountsWithFile();

    }

    //Getters
    public int getSize() {
        return bank.size();
    }

    public double getSumBalances() {
        return bank.values().stream()
                .mapToDouble(AccountCurrent::getBalance)
                .sum();
    }

    public Path getStatementPath() {
        return statementPath;
    }

    public AccountCurrent getAccountById(int accountId) {
        return bank.get(accountId); //retorna a referencia na memoria;
    }

    public Map<Integer, AccountCurrent> getAllAccounts() {
        return Collections.unmodifiableMap(bank); // o cara que importar isso n vai poder alterar a poha do seu ponteiro 
    }

    //Setters
    public void addAccount(AccountCurrent account) throws InvalidInputException {
        if (bank.containsKey(account.getId())) {
            throw new InvalidInputException("Já existe uma conta com esse ID.");
        }
        bank.put(account.getId(), account);
    }

    //Methods
    public int saveAllAccountstToFile() throws IOException {
        AccountCurrentDAO accountDAO = new AccountCurrentDAO();
        int updatesInAccounts = 0;

        try (Connection conn = Conexao.getConnection()) {
            for (Map.Entry<Integer, AccountCurrent> entry : bank.entrySet()) {
                AccountCurrent bankAccount = entry.getValue();
                AccountCurrent dbAccount = accountDAO.getAccount(bankAccount.getId());
                
                if(dbAccount == null){
                    boolean isInsered = accountDAO.insertAccount(bankAccount);
                    if(isInsered){
                        continue;
                    }
                    continue;
                }

                boolean isEqualsHolder = false;
                if (bankAccount.getHolder().equals(dbAccount.getHolder())) {
                    isEqualsHolder = true;
                }
                boolean isEqualsBalance = false;
                if (bankAccount.getBalance() == dbAccount.getBalance()) {
                    isEqualsBalance = true;
                }

                if (!isEqualsHolder) {
                    int sucesses = accountDAO.updateHolder(bankAccount.getId(), bankAccount.getHolder());
                    if (sucesses > 0) {
                        updatesInAccounts++;
                    }
                }
                if (!isEqualsBalance) {
                    int sucesses = accountDAO.updateBalance(bankAccount.getId(), bankAccount.getBalance());
                    if (sucesses > 0) {
                        updatesInAccounts++;
                    }
                }

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "technical problems in database, try later\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );

        }

        // cria o string de contas que vai ficar no cache, bem merda msm uma hora vai dar crash na memoriakk -- fallback do banco
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, AccountCurrent> entry : bank.entrySet()) {
            AccountCurrent account = entry.getValue();
            sb.append(String.valueOf(account.getId())).append(";");
            sb.append(String.valueOf(account.getHolder())).append(";");
            sb.append(String.valueOf(account.getBalance())).append(";\n");
        }

        Files.writeString(this.filepath, sb.toString(), StandardOpenOption.TRUNCATE_EXISTING);
        return updatesInAccounts;
    }

    public void refreshAllAccountsWithFile() throws IOException {
        AccountCurrentDAO accountDAO = new AccountCurrentDAO();
        Map<Integer, AccountCurrent> dbBank = accountDAO.getAllAccounts();
        
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

        } else{
            bank.clear();
            bank.putAll(dbBank);
        }

    }

}
