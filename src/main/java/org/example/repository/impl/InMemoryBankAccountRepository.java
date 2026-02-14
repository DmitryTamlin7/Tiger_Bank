package org.example.repository.impl;

import org.example.domain.BankAccount;
import org.example.repository.BankAccountRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryBankAccountRepository implements BankAccountRepository {

    private final Map<Long, BankAccount> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public BankAccount save(BankAccount account) {
        if (account.getId() == null){
            account.setId(idGenerator.getAndIncrement());
        }
        storage.put(account.getId(), account);
        return account;
    }

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
