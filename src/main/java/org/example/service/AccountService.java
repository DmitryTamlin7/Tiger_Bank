package org.example.service;

import org.example.domain.Operation;

import java.math.BigDecimal;

public interface AccountService {

    Operation createOperation(Long account_id, Long category_id, BigDecimal amount, String description);

    void recalculateBalance(Long account_id);
}
