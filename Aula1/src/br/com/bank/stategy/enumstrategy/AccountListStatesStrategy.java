/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.stategy.enumstrategy;

import br.com.bank.model.Account;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author luskahz
 */
public class AccountListStatesStrategy {

    private final Set<Predicate<Account>> activePredicates = new LinkedHashSet<>();
    private final Set<Comparator<Account>> activeComparators = new LinkedHashSet<>();

    public void togglePredicate(Predicate<Account> predicate) {
        if (activePredicates.contains(predicate)) {
            activePredicates.remove(predicate);
        } else {
            activePredicates.add(predicate);
        }
    }

    public void toggleComparator(Comparator<Account> comparator) {
        if (activeComparators.contains(comparator)) {
            activeComparators.remove(comparator);
        } else {
            activeComparators.add(comparator);
        }
    }

    public Predicate<Account> combinedPredicate() {
        return activePredicates.stream()
                .reduce(ac -> true, Predicate::and);
    }

    public Comparator<Account> combinedComparator() {// entender!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        return activeComparators.stream()
                .reduce((c1, c2) -> c1.thenComparing(c2))
                .orElse(Comparator.comparing(Account::getId));
    }
    
    
    
    
    public List<Account> applyStrategies(Collection<Account> accounts) {
        return accounts.stream()
                .filter(combinedPredicate())
                .sorted(combinedComparator())
                .collect(Collectors.toList());
    }
    
    
    
    
    
    public boolean isPredicateActive(Predicate<Account> predicate) {
        return activePredicates.contains(predicate);
    }

    public boolean isComparatorActive(Comparator<Account> comparator) {
        return activeComparators.contains(comparator);
    }

    public void clear() {
        activePredicates.clear();
        activeComparators.clear();
    }

}
