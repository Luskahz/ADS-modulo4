/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.model;

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

    private Map<Integer, AccountCurent> account;

    public void addAccount(AccountCurent conta) {
        account.put(conta.getNumero(), conta);
    }

    public Bank(Map<Integer, AccountCurent> contas) {
        this.account = contas;
    }

    public Account getAccountByNumber(int numeroConta) {
        return account.get(numeroConta);
    }

    public Map<Integer, AccountCurent> getAllAccounts() {
        return account;
    }

    public int howMuchAccounts() {
        return account.size();
    }

    public void saveAccountsInCache() throws IOException {
        //define o path e o stringbuilder
        Path path = Paths.get("Accounts.txt");
        StringBuilder sb = new StringBuilder();

        // cria o string de contas que vai ficar no cache, bem merda msm uma hora vai dar crash na memoriakk
        for (Map.Entry<Integer, AccountCurent> entry : account.entrySet()) {
            AccountCurent conta = entry.getValue();

            sb.append(conta.getNumero()).append(";");
            sb.append(conta.getTitular()).append(";");
            sb.append(conta.getSaldo()).append(";");
        }

        Files.writeString(path, sb.toString(), StandardOpenOption.TRUNCATE_EXISTING);

    }

    public void refreshAllAcountsFromCache() throws IOException {
        Path path = Paths.get("Accounts.txt");
        account.clear();

        List<String> linhas = Files.readAllLines(path);

        for (String linha : linhas) {
            String[] partes = linha.split(";");
            int numero = Integer.parseInt(partes[0]);
            double saldo = Double.parseDouble(partes[2]);

            AccountCurent conta = new AccountCurent(numero, partes[1], saldo);
            addAccount(conta);

        }

    }

}
