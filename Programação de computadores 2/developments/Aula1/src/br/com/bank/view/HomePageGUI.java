/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.bank.view;

import br.com.bank.model.Bank;
import java.util.Objects;
import javax.swing.JFrame;

/**
 *
 * @author lucas
 */
public class HomePageGUI extends javax.swing.JFrame {

    private final Bank bank;

    public HomePageGUI(Bank bank) {
        this.bank = Objects.requireNonNull(bank, "Bank can't be null");

        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public HomePageGUI() {
        this.bank = new Bank();
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
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

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 150));

        bntCreateAccount.setText("Create Account");
        bntCreateAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCreateAccountActionPerformed(evt);
            }
        });
        jPanel2.add(bntCreateAccount);

        btnListOfAccounts.setText("List of Accounts");
        btnListOfAccounts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListAccountsActionPerformed(evt);
            }
        });
        jPanel2.add(btnListOfAccounts);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonLeaveFromApplication(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLeaveFromApplication
        System.exit(0);
    }//GEN-LAST:event_buttonLeaveFromApplication

    private void bntCreateAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCreateAccountActionPerformed
        CreateAccountGUI dialog = new CreateAccountGUI(this, true, bank);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_bntCreateAccountActionPerformed

    private void btnListAccountsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListAccountsActionPerformed
        ListAccountsGUI dialog = new ListAccountsGUI(this, true, bank);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnListAccountsActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomePageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomePageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomePageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomePageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomePageGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntCreateAccount;
    private javax.swing.JButton btnListOfAccounts;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
