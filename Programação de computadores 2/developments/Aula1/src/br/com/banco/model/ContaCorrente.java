/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.banco.model;

import br.com.banco.exceptions.SaldoInsuficienteException;

public class ContaCorrente extends Conta {

    public ContaCorrente(String numero_parametro, String titular, String saldo_parametro) {
        int numero = Integer.parseInt(numero_parametro);
        double saldo = Double.parseDouble(saldo_parametro);
        
        super(numero, titular, saldo);
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {

        if (valor > super.getSaldo()) {
            throw new SaldoInsuficienteException("Sem saldo suficiente para realizar a acao de sacar...");
        } else {
           double newSaldo = super.getSaldo()- valor;
           super.setSaldo(newSaldo);

        }

    }

}
