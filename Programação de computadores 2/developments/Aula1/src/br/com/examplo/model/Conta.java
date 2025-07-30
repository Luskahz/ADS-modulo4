/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.examplo.model;

import java.nio.file.*;

/**
 *
 * @author cg3034186
 */
public abstract class Conta {

    public Conta(int numero, String titular, double saldo) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldo;
    }

    int numero;
    String titular;
    double saldo;

    public int getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public void depositar(double valor) {
        this.saldo = this.saldo + valor;
    }

    public String imprimirDados() {
        System.out.println("""
                           Dados da conta: 
                           Numero: """ + this.numero + "\nTitular: " + this.titular + "Saldo: " + this.saldo + "\n\n");
        return " Numero:" + this.numero + "\nTitular: " + this.titular + "Saldo: " + this.saldo + "\n\n";

    }

}
