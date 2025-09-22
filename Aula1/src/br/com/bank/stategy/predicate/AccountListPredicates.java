/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.stategy.predicate;

import br.com.bank.model.Account;
import java.util.function.Predicate;

/**
 *
 * @author luskahz
 */
public class AccountListPredicates {

    public static final Predicate<Account> BALANCE_OVER_5000 = acc -> acc.getBalance() > 5000;
    public static final Predicate<Account> PAIR_ID_ACCOUNTS = acc -> (acc.getId() % 2) == 0;
    
}
