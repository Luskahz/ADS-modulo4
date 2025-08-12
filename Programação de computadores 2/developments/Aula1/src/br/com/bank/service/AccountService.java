/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.service;

import br.com.bank.exceptions.InsufficientBalanceException;
import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.model.Account;
import br.com.bank.model.AccountCurrent;
import br.com.bank.model.Bank;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author lucas
 */
public class AccountService {

    public void withdraw(Account account, double valueForWithdraw) throws InsufficientBalanceException, InvalidInputException {
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to withdraw");
        }
        if (valueForWithdraw <= 0) {
            throw new InvalidInputException("Insert a valid Value to withdraw");
        }

        account.withdraw(valueForWithdraw);
    }

    public void deposit(Account account, double valueForDeposit) throws InvalidInputException {
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to withdraw");
        }
        if (valueForDeposit <= 0) {
            throw new InvalidInputException("Insert a valid Value to withdraw");
        }

        account.deposit(valueForDeposit);

    }

    public Map<Integer, AccountCurrent> listAccounts(Bank bank) throws InvalidInputException {
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to list");
        }
        return bank.getAllAccounts();

    }

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

    

    public AccountCurrent createAccount(int id, String holder, double balance) throws InvalidInputException {
        if (id <= 0) {
            throw new InvalidInputException("Insert a valid number of yout Account");
        }
        if ("".equals(holder)) {
            throw new InvalidInputException("Insert a valid Holder");
        }
        if (balance < 0) {
            throw new InvalidInputException("you can't start in debit");
        }
        AccountCurrent account;
        return account = new AccountCurrent(id, holder, balance);

    }

}
