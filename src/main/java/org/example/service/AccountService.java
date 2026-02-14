package org.example.service;

import org.example.domain.Operation;

import java.math.BigDecimal;

public interface AccountService {

    Operation createOperation(Long account_id, Long category_id, BigDecimal amount, String description);
    void recalculateBalance(Long account_id);
    void deleteAccount(Long id);
    void deleteOperation(Long id);
    void updateAccountName(Long id, String newName);
    void deleteCategory(Long id);
    void updateCategoryName(Long id, String newName);
}
