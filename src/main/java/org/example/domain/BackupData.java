package org.example.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class BackupData {
    private List<BankAccount> Accounts;
    private  List<Category> categories;
    private  List<Operation> operations;
}
