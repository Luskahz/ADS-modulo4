/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.dao;

import br.com.bank.model.Account;
import br.com.bank.model.AccountCurrent;
import br.com.bank.model.AccountPayroll;
import br.com.bank.model.AccountSaving;
import br.com.bank.model.Bank;
import br.com.bank.service.AccountService;
import br.com.bank.service.BankService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author luskahz
 */
public class BankDAO {

    AccountDAO accountDAO = new AccountDAO();

    public Map<Integer, Account> getAllAccounts() {
        Map<Integer, Account> bank = new HashMap<>();
        String sql = "SELECT * FROM digitalBank.accounts";//se o banco for grande vai quebrar, mas tudo bem, n vai ficar grande

        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int accountId = rs.getInt("id");
                    String holder = rs.getString("holder");
                    double balance = rs.getDouble("balance");
                    String type = rs.getString("type_account");

                    if ("CURRENT".equals(type)) {
                        AccountCurrent accountCurrent = new AccountCurrent(accountId, holder, balance);
                        bank.put(accountCurrent.getId(), accountCurrent);
                    } else if ("PAYROLL".equals(type)) {
                        AccountPayroll accountPayroll = new AccountPayroll(accountId, holder, balance);
                        bank.put(accountPayroll.getId(), accountPayroll);
                    } else if ("SAVING".equals(type)) {
                        AccountSaving accountSaving = new AccountSaving(accountId, holder, balance);
                        bank.put(accountSaving.getId(), accountSaving);
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
        return bank;
    }

    public boolean InsertTaxationRegister(Instant timeTax) throws SQLException {
        String sql = "insert into digitalBank.taxation_register_global_accounts (application_date) values( ? )";
        java.sql.Timestamp timestampTax = java.sql.Timestamp.from(timeTax);
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, timestampTax);
            int lines = stmt.executeUpdate();
            return lines > 0;
        }
    }

    public boolean applyFeeInBank(Bank bank) throws SQLException {
        Instant lastApplication = null;
        Instant now = Instant.now();
        Map<Integer, Account> TimeExecutionAccounts = bank.getAllAccounts();

        String sql = "SELECT application_date from digitalBank.taxation_register_global_accounts order by application_date desc limit 1;";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp("application_date");
                lastApplication = ts.toInstant();

            }
            long diffInMillis = (lastApplication != null)
                    ? Duration.between(lastApplication, now).toMillis()
                    : 0L;

            int appliTaxHowMuchTimes = (int) (diffInMillis / bank.getTimeToAppliTaxinMills());
            for (Map.Entry<Integer, Account> entry : TimeExecutionAccounts.entrySet()) {
                accountDAO.applyFeeInAccount(entry.getValue(), appliTaxHowMuchTimes); //aplico a taxa, aplico o registro da taxa e altero os valores em tempo de execucao praquela conta
            }
            return InsertTaxationRegister(now);
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
