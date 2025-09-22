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
import br.com.bank.service.AccountService;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author luskahz
 */
public class AccountDAO {

    AccountService accountService = new AccountService();

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
                    if(null == type)return null; else return switch (type) {
                        case "CURRENT" -> new AccountCurrent(accountId, holder, balance);
                        case "PAYROLL" -> new AccountPayroll(accountId, holder, balance);
                        case "SAVING" -> new AccountSaving(accountId, holder, balance);
                        default -> null;
                    };
                    
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

    

    public int updateHolder(int id, String holder) {
        String sql = "UPDATE digitalBank.accounts SET holder = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, holder);
            stmt.setInt(2, id);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "technical problems in database, try later\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );
            return 0;
        }

    }

    public int updateBalance(int id, double balance) {
        String sql = "UPDATE digitalBank.accounts SET balance = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, balance);
            stmt.setInt(2, id);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "technical problems in database, try later\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );
            return 0;
        }

    }

    public boolean insertAccount(Account account) {
        String sql = "INSERT INTO digitalBank.accounts (id, holder, balance, type_account) VALUES(?, ?, ?, ?);";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, account.getId());
            stmt.setString(2, account.getHolder());
            stmt.setDouble(3, account.getBalance());
            stmt.setString(4, account.getTaxationStrategy().name());
            int lines = stmt.executeUpdate();
            return lines > 0;
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

    

    public boolean deleteAccount(Account account) {
        String sql = "DELETE FROM digitalBank.accounts WHERE id = ?;";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, account.getId());
            int lines = stmt.executeUpdate();
            return lines > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro no banco de dados:\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

    }

    public boolean transferForAccount(Account sender, Account receiver, BigDecimal amount)
            throws SQLException, InvalidInputException {

        if (sender.equals(receiver)) {
            throw new InvalidInputException("self transfer");
        }

        String selForUpdate = "SELECT id, balance FROM accounts WHERE id IN (?, ?) FOR UPDATE";
        String debitSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        String creditSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";

        int a = accountService.getId(sender);
        int b = accountService.getId(receiver);
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
    
    
    
    //essa função faz a atualização do saldo no banco, aplica o registro da aplicação da taxa no banco e atualiza o salario no tempo de execução.
    public boolean applyFeeInAccount(Account account, int times) {
        double value_applied = (account.getTaxationStrategy().calculateTax(account.getBalance()))*times;

        account.setBalance(
                account.getBalance() - value_applied
        );

        double balance_after_application = account.getBalance();
        Timestamp application_date = Timestamp.valueOf(LocalDateTime.now());
        updateBalance(account.getId(), balance_after_application);
        String sql = "INSERT INTO digitalBank.taxation_register_individual_account (id_account, fee_form, application_date, value_applied, balance_after_application) VALUES(?, ?, ?, ?, ?);";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, account.getId());
            stmt.setString(2, account.getTaxationStrategy().name());
            stmt.setTimestamp(3, application_date);
            stmt.setDouble(4, value_applied);
            stmt.setDouble(5, account.getBalance());
            int lines = stmt.executeUpdate();
            return lines > 0;

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
    
    

}
