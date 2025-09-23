/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.model;

import br.com.bank.dao.AccountDAO;
import br.com.bank.dao.BankDAO;
import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.stategy.enumstrategy.TaxationStrategy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
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
    private int timeToAppliTaxinMills = 30000; 

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

        refreshAllAccountsWithFileOrDb();

    }

    //Getters
    public int getSize() {
        return bank.size();
    }

    public int getTimeToAppliTaxinMills() {
        return timeToAppliTaxinMills;
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
    public boolean addAccount(Account account) throws InvalidInputException {
        if (bank.containsKey(account.getId())) {
            throw new InvalidInputException("Já existe uma conta com esse ID.");
        }
        bank.put(account.getId(), account);
        return true;
    }

    public boolean removeAccount(int id) {
        return bank.remove(id) != null;
    }

    public boolean saveAllAccountsToFile() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Integer, Account> entry : bank.entrySet()) {
            Account account = entry.getValue();
            sb.append(account.getId()).append(";");
            sb.append(account.getHolder()).append(";");
            sb.append(account.getBalance()).append(";");
            sb.append(account.getTaxationStrategy().name()).append(";\n");
        }

        try {
            Files.writeString(
                    this.filepath,
                    sb.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            return true; 
        } catch (IOException e) {
            e.printStackTrace(); 
            return false; 
        }
    }

    // ao abrir, tenta puxar do banco, se n der puxa do arquivo
    public void refreshAllAccountsWithFileOrDb() throws IOException {
        try {
            Map<Integer, Account> dbBank = bankDAO.getAllAccounts();
            bank.clear();
            bank.putAll(dbBank);
            saveAllAccountsToFile();
        } catch (SQLNonTransientConnectionException e) {
            loadAllAccountsFromFile();
            JOptionPane.showMessageDialog(
                    null,
                    "Database offline, loading accounts from file.",
                    "Database",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {
            loadAllAccountsFromFile();
            JOptionPane.showMessageDialog(
                    null,
                    "Error when refreshing accounts, loading from file instead.\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    //função que de fato puxa do arquivo
    private void loadAllAccountsFromFile() throws IOException {
        bank.clear();

        for (String line : Files.readAllLines(filepath)) {
            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split(";");
            int id = Integer.parseInt(parts[0]);
            String holder = parts[1];
            double balance = Double.parseDouble(parts[2]);
            TaxationStrategy strategy = TaxationStrategy.valueOf(parts[3]);

            Account account = switch (strategy) {
                case CURRENT ->
                    new AccountCurrent(id, holder, balance);
                case SAVING ->
                    new AccountSaving(id, holder, balance);
                case PAYROLL ->
                    new AccountPayroll(id, holder, balance);
            };

            bank.put(id, account);
        }
    }

}
