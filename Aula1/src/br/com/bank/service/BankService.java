/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.service;

import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.model.AccountCurrent;
import br.com.bank.model.Bank;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author lucas
 */
public class BankService {

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
    public Map<Integer, AccountCurrent> listAccounts(Bank bank) throws InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to insert a account");
        }

        return bank.getAllAccounts();

    }

    //Setters
    public void insertAccount(Bank bank, AccountCurrent account) throws IOException, InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to list");
        }
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to insert in bank");
        }
        bank.addAccount(account);

    }

    //Methods
    public AccountCurrent searchAccount(Bank bank, int id) throws InvalidInputException {
        if (id <= 0) {
            throw new InvalidInputException("Insert a valid Id to search a account in the bank, the id have to be a positive number");
        }
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to search within itself");
        }

        return bank.getAccountById(id);

    }
    public int size(Bank bank) throws InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to search within itself");
        }
        return bank.getSize();

    }
    public boolean refresh(Bank bank) throws InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to search within itself");
        }
        try {
            bank.refreshAllAccountsWithFile();
            return true;
        } catch (IOException e) {
            return false;
        }

    }
    public void saveAccounts(Bank bank) throws IOException, InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to save in to file");
        }
        bank.saveAllAccountstToFile();
    }
    public void updateStatement(Bank bank, AccountCurrent account, String message) throws IOException, InvalidInputException {
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
        
        Files.writeString(bank.getStatementPath(), "["+ date.toString() + "] - Account: " + account.getId() + "; Holder: "+ account.getHolder() +"; Balance: " + account.getBalance() + "; [Operation: " + message + "]" +"\n", StandardOpenOption.APPEND);
        
    }
    
    
}
