/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package br.com.bank.app;

import br.com.bank.exceptions.InvalidInputException;
import br.com.bank.model.Bank;
import br.com.bank.service.AccountService;

import br.com.bank.view.HomePageGUI;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        AccountService service = new AccountService();
        javax.swing.SwingUtilities.invokeLater(() -> {
            Bank bank = null;
            try {
                Path path = Paths.get("accounts.txt");
                bank = new Bank(path);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error, we can't create the bank to save the accounts, run after some minutes....\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                bank = new Bank();
            }
            
            HomePageGUI home = new HomePageGUI(bank, service);
            home.setVisible(true);
            
            try{
                service.saveAccounts(bank);
            }catch(IOException | InvalidInputException e){
                System.out.println(e.getMessage());
            }
        });
    }

}
