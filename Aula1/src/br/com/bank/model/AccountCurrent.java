/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.model;

import br.com.bank.exceptions.InsufficientBalanceException;
import br.com.bank.stategy.enumstrategy.TaxationStrategy;

public class AccountCurrent extends Account {

    public AccountCurrent(int id, String holder, double balance) {
        super(id, holder, balance, TaxationStrategy.CURRENT);
    }

    @Override
    public void withdraw(double value) throws InsufficientBalanceException {
        if (value > super.getBalance()) {
            throw new InsufficientBalanceException("Insufficient balance to perform the withdrawal action...");
        }
        // não altera o saldo, só valida
    }

}
