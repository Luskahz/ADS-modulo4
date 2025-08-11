/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.model;

import br.com.bank.exceptions.InsufficientBalanceException;
import java.nio.file.*;

/**
 *
 * @author cg3034186
 */
public abstract class Account {

    int id;
    String holder;
    double balance;

    //construtor
    public Account(int numero, String titular, double saldo) {
        this.id = numero;
        this.holder = titular;
        this.balance = saldo;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getHolder() {
        return holder;
    }

    public double getBalance() {
        return balance;
    }

    //setters
    public void setId(int id) {
        this.id = id;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public void setBalance(double value) {
        this.balance = value;
    }

    public abstract void withdraw(double value) throws InsufficientBalanceException;

    public void deposit(double value) {
        this.balance = this.balance + value;
    }

    public String getAccountFormated() {
        return this.id + ";" + this.holder + ";" + this.balance + ";\n";

    }

}
