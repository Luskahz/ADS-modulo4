/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package br.com.exemplo.app;

import br.com.examplo.model.ContaCorrente;
import br.com.examplo.model.SaldoInsuficienteException;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author cg3034186
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Path caminho = Paths.get("conta.txt");
        Path caminho_update = Paths.get("conta_atualizações.txt");
        List<String> linhas = null;
        try {
            linhas = Files.readAllLines(caminho);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String conta = linhas.get(0);
        String[] dados = conta.split(",");

        int numero = Integer.parseInt(dados[0].trim());
        String titular = dados[1].trim();
        double saldo = Double.parseDouble(dados[2].trim());

        ContaCorrente c = new ContaCorrente(numero, titular, saldo);

        Scanner sc = new Scanner(System.in);
        double valor = 0.0;
        while (valor >= 0.0) {
            System.out.println("escreva um valor para saque:");
            valor = sc.nextDouble();

            try {
                c.sacar(valor);
            } catch (SaldoInsuficienteException e) {
                System.out.println(e.getMessage());
            }

            String update = c.imprimirDados();
            try {
                Files.write(caminho_update, update.getBytes(), StandardOpenOption.APPEND);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }

}
