/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.dao;

import br.com.bank.exceptions.InsufficientBalanceException;
import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.model.AccountCurrent;
import br.com.bank.service.AccountService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author cg3034186
 */
public class AccountCurrentDAO {
    AccountService accountService = new AccountService();
    public void insert(AccountCurrent account) {

    }

    public AccountCurrent getAccount(int id) {
        String sql = "SELECT * FROM digitalBank.accounts WHERE id = ?";

        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int accountId = rs.getInt("id");
                    String holder = rs.getString("holder");
                    double balance = rs.getDouble("balance");
                    return new AccountCurrent(accountId, holder, balance);
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

    public Map<Integer, AccountCurrent> getAllAccounts() {
        Map<Integer, AccountCurrent> bank = new HashMap<>();
        String sql = "SELECT * FROM digitalBank.accounts";//se o banco for grande vai quebrar, mas tudo bem, n vai ficar grande

        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int accountId = rs.getInt("id");
                    String holder = rs.getString("holder");
                    double balance = rs.getDouble("balance");
                    AccountCurrent account = new AccountCurrent(accountId, holder, balance);
                    bank.put(account.getId(), account);
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
        return bank;
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

    public boolean insertAccount(AccountCurrent account) {
        String sql = "INSERT INTO digitalBank.accounts (id, holder, balance) VALUES(?, ?, ?);";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, account.getId());
            stmt.setString(2, account.getHolder());
            stmt.setDouble(3, account.getBalance());
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

    public boolean deleteAccount(AccountCurrent account) {
        String sql = "DELETE FROM digitalbank.accounts WHERE id = ?;";
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
    
    
    public boolean transferForAccount(AccountCurrent sender, AccountCurrent receiver, BigDecimal amount)
        throws SQLException, InvalidInputException {

    if (sender.equals(receiver)) throw new InvalidInputException("self transfer");

    String selForUpdate = "SELECT id, balance FROM accounts WHERE id IN (?, ?) FOR UPDATE";
    String debitSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
    String creditSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";

    int a = accountService.getId(sender);
    int b = accountService.getId(receiver);
    int first = Math.min(a,b), second = Math.max(a,b);

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
                    if (id == a) senderBal = bal;
                    if (id == b) receiverBal = bal;
                }
            }
        }
        if (senderBal == null || receiverBal == null) throw new InvalidInputException("account not found");
        if (senderBal.compareTo(amount) < 0) throw new InvalidInputException("insufficient balance");

        try (PreparedStatement debit = conn.prepareStatement(debitSql);
             PreparedStatement credit = conn.prepareStatement(creditSql)) {

            debit.setBigDecimal(1, amount);
            debit.setInt(2, a);
            if (debit.executeUpdate() != 1) throw new SQLException("debit failed");

            credit.setBigDecimal(1, amount);
            credit.setInt(2, b);
            if (credit.executeUpdate() != 1) throw new SQLException("credit failed");
        }

        conn.commit();
        return true;
    } catch (SQLException | InvalidInputException e) {
        throw e;
    }
}
}
    

