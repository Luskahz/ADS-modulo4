/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.service;

import br.com.bank.dao.AccountDAO;
import br.com.bank.dao.BankDAO;
import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.model.Account;
import br.com.bank.model.Bank;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author lucas
 */
public class BankService {

    BankDAO bankDAO = new BankDAO();
    AccountDAO accountDAO = new AccountDAO();

    public BankDAO getBankDAO() {
        return bankDAO;
    }

    //Constructor
    public Bank createBank(String pathStringBank, String pathStringStatement) throws IOException {
        Path pathBank = Paths.get(pathStringBank);
        Path pathStatement = Paths.get(pathStringStatement);
        Bank bank = new Bank(pathBank, pathStatement);
        return bank;
    }

    //Getters
    public Path getStatementFilepath(Bank bank) throws InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to save in to get StatementPath");
        }
        return bank.getStatementPath();
    }

    public Map<Integer, Account> listAccounts(Bank bank) {
        return bank.getAllAccounts();
    }

    //Setters
    public boolean insertAccount(Bank bank, Account account) throws IOException, InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to list");
        }
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to insert in bank");
        }
        return bank.addAccount(account);

    }

    public void removeAccount(Bank bank, int id) {
        if (accountDAO.deleteAccount(id, bank)) {
            bank.removeAccount(id);
        }

    }

    //Methods
    public Account searchAccount(Bank bank, int id) throws InvalidInputException {
        if (id <= 0) {
            throw new InvalidInputException("Insert a valid Id to search a account in the bank, the id have to be a positive number");
        }
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to search within itself");
        }

        return bank.getAccountById(id);

    }

    public int size(Bank bank) {
        return bank.getSize();

    }

    public double balanceSum(Bank bank) {
        return bank.getSumBalances();
    }

    public boolean refresh(Bank bank) throws InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to search within itself");
        }
        try {
            bank.refreshAllAccountsWithFileOrDb();
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    public void updateStatement(Bank bank, Account account, String message) throws IOException, InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to get the Statement Path");
        }
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to to save in informations of the transition");
        }
        if (message == null) {
            throw new InvalidInputException("Insert a observation for the transition");
        }
        Date date = new Date();

        Files.writeString(bank.getStatementPath(), "[" + date.toString() + "] - Account: " + account.getId() + "; Holder: " + account.getHolder() + "; Balance: " + account.getBalance() + "; [Operation: " + message + "]" + "\n", StandardOpenOption.APPEND);

    }

    public void refreshBankFromdb(Bank bank) throws IOException {
        bank.refreshAllAccountsWithFileOrDb();
    }

    public boolean applyFee(Bank bank) throws SQLException {
        Instant lastApplication = bankDAO.getLastTaxationApplication();
        Instant now = Instant.now();

        int applyTaxHowManyTimes;

        if (lastApplication == null) {
            applyTaxHowManyTimes = 1;
        } else {
            long diffInMillis = Duration.between(lastApplication, now).toMillis();
            applyTaxHowManyTimes = (int) (diffInMillis / bank.getTimeToAppliTaxinMills());
        }

        if (applyTaxHowManyTimes <= 0) {
            return false;
        }

        for (Account account : bank.getAllAccounts().values()) {
            accountDAO.applyFeeInAccount(account, applyTaxHowManyTimes);
        }

        return bankDAO.insertTaxationRegister(now);
    }

    public boolean saveAccountsToFile(Bank bank) throws InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to list");
        }
        return bank.saveAllAccountsToFile();

    }

}
