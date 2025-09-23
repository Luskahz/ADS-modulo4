/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.model;
import br.com.bank.exceptions.InsufficientBalanceException;
import br.com.bank.stategy.enumstrategy.TaxationStrategy;

/**
 *
 * @author cg3034186
 */
public abstract class Account {

    private int id;
    private String holder;
    private double balance;
    private TaxationStrategy taxationStrategy;

    //construtor
    public Account(int number, String holder, double balance, TaxationStrategy taxationStrategy) {
        this.id = number;
        this.holder = holder;
        this.balance = balance;
        this.taxationStrategy = taxationStrategy;
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
    public TaxationStrategy getTaxationStrategy() {
        return taxationStrategy;
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
    public void setTaxationStrategy(TaxationStrategy taxationStrategy) {
        this.taxationStrategy = taxationStrategy;
    }
    
    
    
    public abstract void withdraw(double value) throws InsufficientBalanceException;
    

    public void deposit(double value) {
        this.balance = this.balance + value;
    }
    public String getAccountFormated() {
        return this.id + ";" + this.holder + ";" + this.balance + ";\n";

    }
    
    public double applyTax() {
        return taxationStrategy.calculateTax(balance);
    }
    
}
