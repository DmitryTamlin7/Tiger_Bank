package org.example.repository.impl;

import org.example.domain.Operation;
import org.example.repository.OperationRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryOperationRepository implements OperationRepository {

    private final Map<Long, Operation> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Operation save(Operation operation) {
        if (operation.getId() == null){
            operation.setId(idGenerator.getAndIncrement());
        }
        storage.put(operation.getId(), operation);
        return operation;
    }

    @Override
    public Optional<Operation> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Operation> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }


    @Override
    public List<Operation> findByBankAccountId(Long account_id) {
        return storage.values().stream()
                .filter(o -> o.getBankAccountId().equals(account_id))
                .collect(Collectors.toList());
    }
}
