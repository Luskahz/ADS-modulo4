/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.stategy.enumstrategy;

/**
 *
 * @author luskahz
 */
public enum TaxationStrategy {
    CURRENT{
        @Override
        public double calculateTax(double balance){
            return balance * 0.01; // desconta 3% ao mes com base no balance atual da conta
        }
    },
    SAVING{
        @Override
        public double calculateTax(double balance){
            return 34.90; // desconta uma taxa fixa do usuario
        }
    },
    PAYROLL{
        @Override
        public double calculateTax(double balance){
            return 0.0; //valor caso isento de tributação
        }
    };
    
    public abstract double calculateTax(double balance);
}
