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

    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);

        try (ResultSet rs = stmt.executeQuery()) {   
            if (rs.next()) {
                int accountId = rs.getInt("id");
                String holder  = rs.getString("holder");
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



    public void updateHolder(int id, String holder) {

    }

    public void updateBalance(int id, double balance) {

    }
}
