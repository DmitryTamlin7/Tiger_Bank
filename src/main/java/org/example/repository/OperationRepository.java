package org.example.repository;

import org.example.domain.Category;
import org.example.domain.Operation;

import java.util.List;
import java.util.Optional;

public interface OperationRepository {
    Operation save(Operation operation);
    Optional<Operation> findById(Long id);
    List<Operation> findAll();
    void deleteById(Long id);
    List<Operation> findByBankAccountId(Long account_id);
    void deleteAll();
}
