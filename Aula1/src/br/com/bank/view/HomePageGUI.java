/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.bank.view;

import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.model.Bank;
import br.com.bank.service.AccountService;
import br.com.bank.service.BankService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author lucas
 */
public class HomePageGUI extends javax.swing.JFrame {

    private final Bank bank;
    private final AccountService accountService;
    private final BankService bankService;

    public HomePageGUI(Bank bank, AccountService accountService, BankService bankService) {
        this.bank = Objects.requireNonNull(bank);
        this.accountService = accountService;
        this.bankService = bankService;

        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        Title = new javax.swing.JPanel();
        TitleLabel = new javax.swing.JLabel();
        CGlabel = new javax.swing.JLabel();
        Content = new javax.swing.JPanel();
        bntCreateAccount = new javax.swing.JButton();
        btnListOfAccounts = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("home");

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jButton1.setText("Exit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLeaveFromApplication(evt);
            }
        });
        jPanel1.add(jButton1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setLayout(new java.awt.GridLayout(3, 0));

        Title.setLayout(new java.awt.GridLayout(2, 0));

        TitleLabel.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        TitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TitleLabel.setText("Welcome to DigitalBank");
        Title.add(TitleLabel);

        CGlabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CGlabel.setText("CG3034186 - Lucas Alves");
        Title.add(CGlabel);

        jPanel2.add(Title);

        bntCreateAccount.setText("Create Account");
        bntCreateAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCreateAccountActionPerformed(evt);
            }
        });
        Content.add(bntCreateAccount);

        btnListOfAccounts.setText("List of Accounts");
        btnListOfAccounts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListAccountsActionPerformed(evt);
            }
        });
        Content.add(btnListOfAccounts);

        jPanel2.add(Content);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonLeaveFromApplication(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLeaveFromApplication
        try {
            bankService.saveAccountsToFile(bank);
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input Error", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);
    }//GEN-LAST:event_buttonLeaveFromApplication

    private void bntCreateAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCreateAccountActionPerformed
        this.setVisible(false);

        CreateAccountGUI dialog = new CreateAccountGUI(this, true, bank, accountService, bankService);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        this.setVisible(true);
    }//GEN-LAST:event_bntCreateAccountActionPerformed

    private void btnListAccountsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListAccountsActionPerformed
        this.setVisible(false);
        try {
            bankService.applyFee(bank);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Fee applycation Error - SQL error", JOptionPane.ERROR_MESSAGE);
        }
        ListAccountsGUI dialog = new ListAccountsGUI(this, true, bank, accountService, bankService);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        this.setVisible(true);
    }//GEN-LAST:event_btnListAccountsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CGlabel;
    private javax.swing.JPanel Content;
    private javax.swing.JPanel Title;
    private javax.swing.JLabel TitleLabel;
    private javax.swing.JButton bntCreateAccount;
    private javax.swing.JButton btnListOfAccounts;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
