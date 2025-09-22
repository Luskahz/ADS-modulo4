/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.stategy.comparator;

import br.com.bank.model.Account;
import java.util.Comparator;

/**
 *
 * @author luskahz
 */
public class AccountListComparators {
        public static final Comparator<Account> ORDER_BY_BALANCE_ASC = (acc1, acc2) -> Double.compare(acc1.getBalance(), acc2.getBalance());
        public static final Comparator<Account> ORDER_BY_BALANCE_DESC = (acc1, acc2) -> Double.compare(acc2.getBalance(), acc1.getBalance());
        public static final Comparator<Account> ORDER_BY_HOLDER_ASC = (acc1, acc2) -> acc1.getHolder().compareToIgnoreCase(acc2.getHolder());
        
}
