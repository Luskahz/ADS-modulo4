/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.stategy.enumstrategy;

/**
 *
 * @author luskahz
 */
public enum FeeStrategy {
    BASIC {
        @Override
        public double calculateFee(double balance) {
            return balance * 0.02; // 2% do saldo
        }
    },
    PREMIUM {
        @Override
        public double calculateFee(double balance) {
            return 49.90; // tarifa fixa
        }
    },
    ENTERPRISE {
        @Override
        public double calculateFee(double balance) {
            return (balance > 10000) ? 199.90 : 99.90; // cobra mais para saldos grandes
        }
    },
    STUDENT {
        @Override
        public double calculateFee(double balance) {
            return 5.00; // taxa simbólica para estudantes
        }
    };

    public abstract double calculateFee(double balance);
}
