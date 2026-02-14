package org.example.service.imp;

import org.example.domain.BankAccount;
import org.example.domain.Category;
import org.example.domain.Operation;
import org.example.domain.OperationType;
import org.example.repository.BankAccountRepository;
import org.example.repository.CategoryRepository;
import org.example.repository.OperationRepository;
import org.example.service.AccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final BankAccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;

    public AccountServiceImpl(BankAccountRepository accountRepository,
                              CategoryRepository categoryRepository,
                              OperationRepository operationRepository){
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public Operation createOperation(Long accountId, Long categoryId, BigDecimal amount, String description) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма операции должна быть больше нуля! 🐯");
        }

        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Такого счета нет"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Такой категории нет"));

        Operation operation = new Operation(
                null,
                accountId,
                categoryId,
                category.getOperationType(),
                amount,
                LocalDateTime.now(),
                description
        );

        if (category.getOperationType() == OperationType.INCOME) {
            account.setBalance(account.getBalance().add(amount));
        } else {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Недостаточно средств!");
            }
            account.setBalance(account.getBalance().subtract(amount));
        }
        accountRepository.save(account);
        return operationRepository.save(operation);
    }

    @Override
    public void recalculateBalance(Long accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Такого счета нет"));

        List<Operation> operations = operationRepository.findByBankAccountId(accountId);

        BigDecimal calculateBalance = operations.stream()
                .map(op -> op.getOperationType() == OperationType.INCOME
                ? op.getAmount() : op.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        account.setBalance(calculateBalance);
        accountRepository.save(account);

        System.out.println("Баланс счета пересчитан " + calculateBalance);
    }

    @Override
    public void deleteOperation(Long operationId) {
        Operation op = operationRepository.findById(operationId).orElseThrow();
        Long accId = op.getBankAccountId();
        operationRepository.deleteById(operationId);
        recalculateBalance(accId);
    }

    @Override
    public void updateAccountName(Long id, String newName) {
        BankAccount acc = accountRepository.findById(id).orElseThrow();
        acc.setName(newName);
        accountRepository.save(acc);
    }

    @Override
    public void deleteAccount(Long id) {
        operationRepository.deleteById(id);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void updateCategoryName(Long id, String newName) {
        Category cat = categoryRepository.findById(id).orElseThrow();
        cat.setName(newName);
        categoryRepository.save(cat);
    }
}
