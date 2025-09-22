/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.service;

import br.com.bank.exceptions.InsufficientBalanceException;
import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.model.Account;
import br.com.bank.model.AccountCurrent;
import br.com.bank.model.AccountPayroll;
import br.com.bank.model.AccountSaving;
import br.com.bank.model.Bank;
import br.com.bank.service.BankService;
import java.io.IOException;

/**
 *
 * @author lucas
 */
public class AccountService {

    public Account createAccount(int id, String holder, double balance, String type) throws InvalidInputException {
        if (id <= 0) {
            throw new InvalidInputException("Insert a valid number of yout Account");
        }
        if ("".equals(holder)) {
            throw new InvalidInputException("Insert a valid Holder");
        }
        if (balance < 0) {
            throw new InvalidInputException("you can't start in debit");
        }
        
        if(null == type){
            throw new InvalidInputException("you have to select a valid account format");
        } else switch (type) {
            case "CURRENT" -> {
                return new AccountCurrent(id, holder, balance);
            }
            case "SAVING" -> {
                return new AccountSaving(id, holder, balance);
            }
            case "PAYROLL" -> {
                return new AccountPayroll(id, holder, balance);
            }
            default -> throw new InvalidInputException("you have to select a valid account format");
        }
    }

    //Constructor
    public AccountCurrent createAccountCurrent(int id, String holder, double balance) throws InvalidInputException {
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

    public AccountSaving createAccountSaving(int id, String holder, double balance) throws InvalidInputException {
        if (id <= 0) {
            throw new InvalidInputException("Insert a valid number of yout Account");
        }
        if ("".equals(holder)) {
            throw new InvalidInputException("Insert a valid Holder");
        }
        if (balance < 0) {
            throw new InvalidInputException("you can't start in debit");
        }

        return new AccountSaving(id, holder, balance);

    }

    public AccountPayroll createAccountPayroll(int id, String holder, double balance) throws InvalidInputException {
        if (id <= 0) {
            throw new InvalidInputException("Insert a valid number of yout Account");
        }
        if ("".equals(holder)) {
            throw new InvalidInputException("Insert a valid Holder");
        }
        if (balance < 0) {
            throw new InvalidInputException("you can't start in debit");
        }

        return new AccountPayroll(id, holder, balance);

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
    public double withdraw(Account account, Bank bank, double valueForWithdraw, BankService bankService) throws InsufficientBalanceException, InvalidInputException, IOException {
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to withdraw");
        }
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to withdraw a value for a account");
        }
        double previousBalance = account.getBalance();

        account.withdraw(valueForWithdraw);
        bankService.updateStatement(bank, account, "Withdraw of $" + valueForWithdraw + "; Previous balance: $" + previousBalance);
        return account.getBalance();
    }

    public double deposit(Account account, Bank bank, double valueForDeposit, BankService bankService) throws InvalidInputException, IOException {
        if (account == null) {
            throw new InvalidInputException("Insert a valid account to deposit");
        }
        if (valueForDeposit <= 0) {
            throw new InvalidInputException("Insert a valid Value to deposit");
        }
        double previousBalance = account.getBalance();
        account.deposit(valueForDeposit);
        bankService.updateStatement(bank, account, "Deposit of $" + valueForDeposit + "; Previous balance: $" + previousBalance);
        return account.getBalance();
    }

}
