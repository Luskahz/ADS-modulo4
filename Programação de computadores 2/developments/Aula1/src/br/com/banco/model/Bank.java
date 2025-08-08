/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.banco.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lucas
 */
public class Bank {

    private Map<Integer, ContaCorrente> contas;

    public void addAccount(ContaCorrente conta) {
        contas.put(conta.getNumero(), conta);
    }

    public Bank(Map<Integer, ContaCorrente> contas) {
        this.contas = contas;
    }

    public Conta getAccountByNumber(int numeroConta) {
        return contas.get(numeroConta);
    }

    public Map<Integer, ContaCorrente> getAllAccounts() {
        return contas;
    }

    public int howMuchAccounts() {
        return contas.size();
    }

    public void saveAccountsInCache() throws IOException {
        //define o path e o stringbuilder
        Path path = Paths.get("Accounts.txt");
        StringBuilder sb = new StringBuilder();

        // cria o string de contas que vai ficar no cache, bem merda msm uma hora vai dar crash na memoriakk
        for (Map.Entry<Integer, ContaCorrente> entry : contas.entrySet()) {
            ContaCorrente conta = entry.getValue();

            sb.append(conta.getNumero()).append(";");
            sb.append(conta.getTitular()).append(";");
            sb.append(conta.getSaldo()).append(";");
        }

        Files.writeString(path, sb.toString(), StandardOpenOption.TRUNCATE_EXISTING);

    }

    public void refreshAllAcountsFromCache() throws IOException {
        Path path = Paths.get("Accounts.txt");
        contas.clear();

        List<String> linhas = Files.readAllLines(path);

        for (String linha : linhas) {
            String[] partes = linha.split(";");
            int numero = Integer.parseInt(partes[0]);
            double saldo = Double.parseDouble(partes[2]);

            ContaCorrente conta = new ContaCorrente(numero, partes[1], saldo);
            addAccount(conta);

        }

    }

}
