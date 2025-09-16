/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.dao;

import br.com.bank.model.AccountCurrent;
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
}
