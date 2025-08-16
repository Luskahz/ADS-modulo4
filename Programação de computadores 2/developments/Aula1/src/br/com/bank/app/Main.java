/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package br.com.bank.app;

import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.model.Bank;
import br.com.bank.service.AccountService;
import br.com.bank.service.BankService;

import br.com.bank.view.HomePageGUI;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author cg3034186
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AccountService accountService = new AccountService();
        BankService bankService = new BankService();
        javax.swing.SwingUtilities.invokeLater(() -> {
            final Bank bank;
            try {
                bank = bankService.createBank("Account.txt", "Statement.txt");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error, we can't create the bank to save the accounts, run after some minutes....\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
                return;
            }

            HomePageGUI home = new HomePageGUI(bank, accountService, bankService);
            home.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            home.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    try {
                        bankService.saveAccounts(bank);
                    } catch (IOException | InvalidInputException ex) {
                        JOptionPane.showMessageDialog(home, "Error saving accounts: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);

                    }

                }
            });
            home.setLocationRelativeTo(null);
            home.setVisible(true);

        });
    }

}
