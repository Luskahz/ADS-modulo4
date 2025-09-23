/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.dao;

import br.com.bank.model.Account;
import br.com.bank.model.AccountCurrent;
import br.com.bank.model.AccountPayroll;
import br.com.bank.model.AccountSaving;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
/**
 *
 * @author luskahz
 */
public class BankDAO {

    public Map<Integer, Account> getAllAccounts() throws SQLException {
        Map<Integer, Account> bank = new HashMap<>();
        String sql = "SELECT * FROM digitalBank.accounts";

        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int accountId = rs.getInt("id");
                String holder = rs.getString("holder");
                double balance = rs.getDouble("balance");
                String type = rs.getString("type_account");

                Account account = switch (type) {
                    case "CURRENT" ->
                        new AccountCurrent(accountId, holder, balance);
                    case "PAYROLL" ->
                        new AccountPayroll(accountId, holder, balance);
                    case "SAVING" ->
                        new AccountSaving(accountId, holder, balance);
                    default ->
                        null;
                };

                if (account != null) {
                    bank.put(account.getId(), account);
                }
            }
        }
        return bank;
    }

    public Instant getLastTaxationApplication() throws SQLException {
        String sql = "SELECT application_date FROM digitalBank.taxation_register_global_accounts "
                + "ORDER BY application_date DESC LIMIT 1";

        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getTimestamp("application_date").toInstant();
            }
        }
        return null;
    }

    public boolean insertTaxationRegister(Instant timeTax) throws SQLException {
        String sql = "INSERT INTO digitalBank.taxation_register_global_accounts (application_date) VALUES (?)";
        java.sql.Timestamp timestampTax = java.sql.Timestamp.from(timeTax);

        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, timestampTax);
            return stmt.executeUpdate() > 0;
        }
    }
    
    
    public boolean insertStatement(int accountId, String operation, String description,
                                   double previousBalance, double newBalance) {
        String sql = "INSERT INTO digitalBank.account_statements " +
                     "(account_id, operation, description, previous_balance, new_balance) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            stmt.setString(2, operation);
            stmt.setString(3, description);
            stmt.setDouble(4, previousBalance);
            stmt.setDouble(5, newBalance);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error inserting statement into database\n" + e.getMessage(),
                    "Database",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }
}
