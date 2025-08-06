/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package br.com.banco.app;

import br.com.banco.view.InterfaceEdicaoContaGUI;

/**
 *
 * @author cg3034186
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InterfaceEdicaoContaGUI().setVisible(true);
            }
        });
    }

}
