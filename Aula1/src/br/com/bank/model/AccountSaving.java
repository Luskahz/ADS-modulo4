/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.model;

import br.com.bank.exceptions.InsufficientBalanceException;
import br.com.bank.stategy.enumstrategy.TaxationStrategy;

/**
 *
 * @author luskahz
 */
public class AccountSaving extends Account {

    public AccountSaving(int id, String holder, double balance) {
        super(id, holder, balance, TaxationStrategy.SAVING);
    }

    @Override
    public void withdraw(double value) throws InsufficientBalanceException {
        if (value > super.getBalance()) {
            throw new InsufficientBalanceException("Sem saldo suficiente para realizar a acao de sacar...");
        } else {
            double newBalance = super.getBalance() - value;
            super.setBalance(newBalance);

        }

    }

}
