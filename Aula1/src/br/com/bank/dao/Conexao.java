/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.bank.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author cg3034186
 */
public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/digitalBank?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "lucas_alves";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
}

// == Estrutura do banco, para a querida professora juliana testar :) == :
/*


create database if not exists digitalBank;

create table digitalBank.`transactions`(
	id_transaction bigint primary key auto_increment , 
    id_account_debit bigint,
    id_account_credit bigint,
    `value` decimal(10,2)
);

CREATE TABLE digitalBank.taxation_register_global_accounts (
    id_taxation BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_date DATETIME NOT NULL
);

CREATE TABLE digitalBank.taxation_register_individual_account (
    id_taxation BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_account BIGINT NOT NULL,
    fee_form VARCHAR(25) NOT NULL,
    application_date DATETIME NOT NULL,
    value_applied DECIMAL(20,2) NOT NULL,
    balance_after_application DECIMAL(20,2) NOT NULL
);


create table digitalBank.accounts (
    id bigint primary key auto_increment,
    holder varchar(100) not null,
    balance decimal(10,2),
    type_account varchar(25) not null

);

CREATE TABLE digitalBank.account_statements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    operation VARCHAR(50) NOT NULL, 
    description TEXT NOT NULL,
    previous_balance DECIMAL(15,2) NOT NULL,
    new_balance DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);



*/