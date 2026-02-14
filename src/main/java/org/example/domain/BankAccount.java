package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {

    private Long id;
    private String name;
    private BigDecimal balance;

    public BankAccount(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }
}
