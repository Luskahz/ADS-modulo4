/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.dao;

import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.model.Account;
import br.com.bank.model.AccountCurrent;
import br.com.bank.model.AccountPayroll;
import br.com.bank.model.AccountSaving;
import br.com.bank.model.Bank;
import br.com.bank.stategy.enumstrategy.TaxationStrategy;
import static br.com.bank.stategy.enumstrategy.TaxationStrategy.CURRENT;
import static br.com.bank.stategy.enumstrategy.TaxationStrategy.PAYROLL;
import static br.com.bank.stategy.enumstrategy.TaxationStrategy.SAVING;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

/**
 *
 * @author luskahz
 */
public class AccountDAO {

    public Account insertAccount(String holder, double balance, TaxationStrategy strategy) {
        String sql = "INSERT INTO digitalBank.accounts (holder, balance, type_account) VALUES (?, ?, ?);";
        Account account = null;
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, holder);
            stmt.setDouble(2, balance);
            stmt.setString(3, strategy.name());

            int lines = stmt.executeUpdate();

            if (lines > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        account = switch (strategy) {
                            case CURRENT ->
                                new AccountCurrent(generatedId, holder, balance);
                            case SAVING ->
                                new AccountSaving(generatedId, holder, balance);
                            case PAYROLL ->
                                new AccountPayroll(generatedId, holder, balance);
                        };
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
        return account;
    }

    public boolean deleteAccount(int id, Bank bank) {
        String sql = "DELETE FROM digitalBank.accounts WHERE id = ?;";
        boolean dbAccount = false;
        boolean bankAccount = false;

        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int lines = stmt.executeUpdate();
            dbAccount = (lines > 0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro no banco de dados:\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        bankAccount = bank.removeAccount(id);

        return (dbAccount || bankAccount);
    }

    public Account getAccount(int id) {
        String sql = "SELECT * FROM digitalBank.accounts WHERE id = ?";

        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int accountId = rs.getInt("id");
                    String holder = rs.getString("holder");
                    double balance = rs.getDouble("balance");
                    String type = rs.getString("type_account");
                    if (null == type) {
                        return null;
                    } else {
                        return switch (type) {
                            case "CURRENT" ->
                                new AccountCurrent(accountId, holder, balance);
                            case "PAYROLL" ->
                                new AccountPayroll(accountId, holder, balance);
                            case "SAVING" ->
                                new AccountSaving(accountId, holder, balance);
                            default ->
                                null;
                        };
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
        return null;
    }

    public boolean updateHolder(Account account, String holder) {
        String sql = "UPDATE digitalBank.accounts SET holder = ? WHERE id = ?";
        boolean dbAccount = false;

        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, holder);
            stmt.setInt(2, account.getId());
            int rows = stmt.executeUpdate();  // executa 1 vez

            if (rows > 0) {
                dbAccount = true;
                account.setHolder(holder); // sincroniza objeto em memória
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "technical problems in database, try later\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return dbAccount;
    }

    public boolean updateBalance(Account account, double balance) {
        String sql = "UPDATE digitalBank.accounts SET balance = ? WHERE id = ?";
        boolean dbAccount = false;
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, balance);
            stmt.setInt(2, account.getId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                dbAccount = true;
                account.setBalance(balance);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "technical problems in database, try later\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );

        }
        return dbAccount;
    }

    public boolean insertStatement(Connection conn,
            int accountId,
            String operation,
            String description,
            double previousBalance,
            double newBalance) throws SQLException {
        String sql = "INSERT INTO digitalBank.account_statements "
                + "(account_id, operation, description, previous_balance, new_balance) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, operation);
            stmt.setString(3, description);
            stmt.setDouble(4, previousBalance);
            stmt.setDouble(5, newBalance);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateType(Account account, TaxationStrategy strategy) {
        String sql = "UPDATE digitalBank.accounts SET type_account = ? WHERE id = ?";
        boolean dbAccount = false;
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, strategy.name());
            stmt.setInt(2, account.getId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                dbAccount = true;
                account.setTaxationStrategy(strategy);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "technical problems in database, can't update the type, try again later\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );

        }
        return dbAccount;
    }

    public boolean transferForAccount(Account sender, Account receiver, BigDecimal amount)
            throws SQLException, InvalidInputException {

        if (sender.equals(receiver)) {
            throw new InvalidInputException("self transfer");
        }

        String selForUpdate = "SELECT id, balance FROM accounts WHERE id IN (?, ?) FOR UPDATE";
        String debitSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        String creditSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";

        int a = sender.getId();
        int b = receiver.getId();
        int first = Math.min(a, b), second = Math.max(a, b);

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);

            BigDecimal senderBal = null, receiverBal = null;

            try (PreparedStatement ps = conn.prepareStatement(selForUpdate)) {
                ps.setInt(1, first);
                ps.setInt(2, second);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        BigDecimal bal = rs.getBigDecimal("balance");
                        if (id == a) {
                            senderBal = bal;
                        }
                        if (id == b) {
                            receiverBal = bal;
                        }
                    }
                }
            }
            if (senderBal == null || receiverBal == null) {
                throw new InvalidInputException("account not found");
            }
            if (senderBal.compareTo(amount) < 0) {
                throw new InvalidInputException("insufficient balance");
            }

            try (PreparedStatement debit = conn.prepareStatement(debitSql); PreparedStatement credit = conn.prepareStatement(creditSql)) {

                debit.setBigDecimal(1, amount);
                debit.setInt(2, a);
                if (debit.executeUpdate() != 1) {
                    throw new SQLException("debit failed");
                }

                credit.setBigDecimal(1, amount);
                credit.setInt(2, b);
                if (credit.executeUpdate() != 1) {
                    throw new SQLException("credit failed");
                }
            }

            conn.commit();
            return true;
        } catch (SQLException | InvalidInputException e) {
            throw e;
        }
    }

    public boolean applyFeeInAccount(Account account, int times) {
        double valueApplied = account.getTaxationStrategy().calculateTax(account.getBalance()) * times;
        double newBalance = account.getBalance() - valueApplied;
        Timestamp applicationDate = Timestamp.valueOf(LocalDateTime.now());

        String updateSql = "UPDATE digitalBank.accounts SET balance = ? WHERE id = ?";
        String insertSql = "INSERT INTO digitalBank.taxation_register_individual_account "
                + "(id_account, fee_form, application_date, value_applied, balance_after_application) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false); // tudo ou nada

            // Atualiza saldo
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setDouble(1, newBalance);
                updateStmt.setInt(2, account.getId());
                if (updateStmt.executeUpdate() <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Insere log
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, account.getId());
                insertStmt.setString(2, account.getTaxationStrategy().name());
                insertStmt.setTimestamp(3, applicationDate);
                insertStmt.setDouble(4, valueApplied);
                insertStmt.setDouble(5, newBalance);

                if (insertStmt.executeUpdate() <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Atualiza em memória só após commit
            conn.commit();
            account.setBalance(newBalance);
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "technical problems in database, try later\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    public boolean updateBalanceDelta(Connection conn, int id, BigDecimal delta) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, delta);
            stmt.setInt(2, id);
            return stmt.executeUpdate() == 1;
        }
    }

    public BigDecimal getBalanceForUpdate(Connection conn, int id) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE id = ? FOR UPDATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
            }
        }
        return null;
    }

    public boolean updateBalance(Connection conn, int id, double balance) throws SQLException {
        String sql = "UPDATE digitalBank.accounts SET balance = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, balance);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean insertTaxationRegister(Connection conn, int accountId, String form,
            Timestamp date, double valueApplied, double balanceAfter) throws SQLException {
        String sql = "INSERT INTO digitalBank.taxation_register_individual_account "
                + "(id_account, fee_form, application_date, value_applied, balance_after_application) "
                + "VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, form);
            stmt.setTimestamp(3, date);
            stmt.setDouble(4, valueApplied);
            stmt.setDouble(5, balanceAfter);
            return stmt.executeUpdate() > 0;
        }
    }

}
