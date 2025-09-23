/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.service;

import br.com.bank.dao.AccountDAO;
import br.com.bank.dao.BankDAO;
import br.com.bank.dao.Conexao;
import br.com.bank.exceptions.InsufficientBalanceException;
import br.com.bank.exceptions.InvalidInputException;
import java.sql.SQLException;
import br.com.bank.model.Account;
import br.com.bank.model.AccountCurrent;
import br.com.bank.model.AccountPayroll;
import br.com.bank.model.AccountSaving;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import br.com.bank.model.Bank;
import br.com.bank.stategy.enumstrategy.TaxationStrategy;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;

/**
 *
 * @author lucas
 */
public class AccountService {

    BankService bankService = new BankService();
    AccountDAO accountDAO = new AccountDAO();
    BankDAO bankDAO = new BankDAO();

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public BankDAO getBankDAO() {
        return bankDAO;
    }

    public boolean createAccount(Bank bank, String holder, double balance, String type)
            throws InvalidInputException, IOException {
        if (holder == null || holder.isBlank()) {
            throw new InvalidInputException("Insert a valid Holder");
        }
        if (balance < 0) {
            throw new InvalidInputException("You can't start in debit");
        }
        if (type == null) {
            throw new InvalidInputException("You have to select a valid account format");
        }

        // Chama o DAO
        Account created = accountDAO.insertAccount(holder, balance, TaxationStrategy.valueOf(type));

        if (created != null) {
            bank.addAccount(created);
            return true;
        } else {
            return false;
        }
    }

    //Constructor
    public Account createAccountCurrent(int id, String holder, double balance) throws InvalidInputException {
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
    public double withdraw(Account account, Bank bank, double valueForWithdraw, BankService bankService)
            throws InsufficientBalanceException, InvalidInputException, IOException {

        if (account == null) {
            throw new InvalidInputException("Insert a valid account to withdraw");
        }
        if (bank == null) {
            throw new InvalidInputException("Insert a valid bank to withdraw a value for an account");
        }

        double previousBalance = account.getBalance();

        if (valueForWithdraw > account.getBalance()) {
            throw new InsufficientBalanceException("Sem saldo suficiente para realizar a ação de sacar...");
        }

        double newBalance = previousBalance - valueForWithdraw;

        boolean dbOk = accountDAO.updateBalance(account, newBalance);

        if (!dbOk) {
            throw new IOException("Failed to update balance in database. Withdraw cancelled.");
        }

        account.setBalance(newBalance);
        bankDAO.insertStatement(
                account.getId(),
                "WITHDRAW",
                "Withdraw of $" + valueForWithdraw + "; Previous balance: $" + previousBalance,
                previousBalance,
                newBalance
        );

        bankService.updateStatement(
                bank,
                account,
                "Withdraw of $" + valueForWithdraw + "; Previous balance: $" + previousBalance
        );

        return account.getBalance();
    }

    public double deposit(Account account, Bank bank, double valueForDeposit, BankService bankService)
            throws InvalidInputException, IOException {

        if (account == null) {
            throw new InvalidInputException("Insert a valid account to deposit");
        }
        if (valueForDeposit <= 0) {
            throw new InvalidInputException("Insert a valid value to deposit");
        }

        double previousBalance = account.getBalance();
        double newBalance = previousBalance + valueForDeposit;

        boolean dbOk = accountDAO.updateBalance(account, newBalance);

        if (!dbOk) {
            throw new IOException("Failed to update balance in database. Deposit cancelled.");
        }

        account.setBalance(newBalance);
        bankDAO.insertStatement(
                account.getId(),
                "DEPOSIT",
                "Deposit of $" + valueForDeposit + "; Previous balance: $" + previousBalance,
                previousBalance,
                newBalance
        );

        bankService.updateStatement(
                bank,
                account,
                "Deposit of $" + valueForDeposit + "; Previous balance: $" + previousBalance
        );

        return account.getBalance();
    }

    public boolean transfer(Account sender, Account receiver, BigDecimal amount)
            throws SQLException, InvalidInputException {

        if (sender.equals(receiver)) {
            throw new InvalidInputException("self transfer");
        }

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);

            BigDecimal senderBal = accountDAO.getBalanceForUpdate(conn, sender.getId());
            BigDecimal receiverBal = accountDAO.getBalanceForUpdate(conn, receiver.getId());

            if (senderBal == null || receiverBal == null) {
                throw new InvalidInputException("account not found");
            }
            if (senderBal.compareTo(amount) < 0) {
                throw new InvalidInputException("insufficient balance");
            }

            BigDecimal newSenderBal = senderBal.subtract(amount);
            BigDecimal newReceiverBal = receiverBal.add(amount);

            boolean debitOk = accountDAO.updateBalanceDelta(conn, sender.getId(), amount.negate());
            boolean creditOk = accountDAO.updateBalanceDelta(conn, receiver.getId(), amount);

            boolean senderLogOk = accountDAO.insertStatement(
                    conn,
                    sender.getId(),
                    "TRANSFER_OUT",
                    "Transfer of $" + amount + " to account " + receiver.getId(),
                    senderBal.doubleValue(),
                    newSenderBal.doubleValue()
            );

            boolean receiverLogOk = accountDAO.insertStatement(
                    conn,
                    receiver.getId(),
                    "TRANSFER_IN",
                    "Transfer of $" + amount + " from account " + sender.getId(),
                    receiverBal.doubleValue(),
                    newReceiverBal.doubleValue()
            );

            if (!debitOk || !creditOk || !senderLogOk || !receiverLogOk) {
                conn.rollback();
                throw new SQLException("transfer failed");
            }

            conn.commit();

            // Atualiza objetos em memória
            sender.setBalance(newSenderBal.doubleValue());
            receiver.setBalance(newReceiverBal.doubleValue());

            return true;
        }
    }

    public boolean applyFee(Account account, int times) throws SQLException {
        double valueApplied = account.getTaxationStrategy().calculateTax(account.getBalance()) * times;
        double newBalance = account.getBalance() - valueApplied;
        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            boolean updated = accountDAO.updateBalance(conn, account.getId(), newBalance);
            boolean logged = accountDAO.insertTaxationRegister(
                    conn,
                    account.getId(),
                    account.getTaxationStrategy().name(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    valueApplied,
                    newBalance
            );
            if (!updated || !logged) {
                conn.rollback();
                return false;
            }
            conn.commit();
            account.setBalance(newBalance);

            return true;
        }
    }

}
