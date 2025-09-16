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
import br.com.bank.service.BankService;
import java.io.IOException;

/**
 *
 * @author lucas
 */
public class AccountService {
    //Constructor
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
        return new AccountCurrent(id, holder, balance);

    }
    
    //Getters
    public int getId(Account account) throws InvalidInputException {
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to get id");
        }
        return account.getId();
    }
    public String getHolder(Account account) throws InvalidInputException {
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to get the holder name");
        }
        return account.getHolder();
    }
    public double getBalance(Account account) throws InvalidInputException {
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to get the balance of account");
        }
        return account.getBalance();
    }

    //Methods
    public double withdraw(AccountCurrent account, Bank bank, double valueForWithdraw, BankService bankService) throws InsufficientBalanceException, InvalidInputException, IOException {
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to withdraw");
        }
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to withdraw a value for a account");
        }
        double previousBalance = account.getBalance();
        
        account.withdraw(valueForWithdraw);
        bankService.updateStatement(bank, account, "Withdraw of $"+ valueForWithdraw+ "; Previous balance: $" + previousBalance);
        return account.getBalance();
    }
    public double deposit(AccountCurrent account, Bank bank, double valueForDeposit, BankService bankService) throws InvalidInputException, IOException {
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to deposit");
        }
        if (valueForDeposit <= 0) {
            throw new InvalidInputException("Insert a valid Value to deposit");
        }
        double previousBalance = account.getBalance();
        account.deposit(valueForDeposit);
        bankService.updateStatement(bank, account, "Deposit of $"+ valueForDeposit+ "; Previous balance: $" + previousBalance);
        return account.getBalance();
    }


    

    

}
