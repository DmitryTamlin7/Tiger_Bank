package org.example.service.imp;

import org.example.domain.Operation;
import org.example.domain.OperationType;
import org.example.repository.BankAccountRepository;
import org.example.repository.CategoryRepository;
import org.example.repository.OperationRepository;
import org.example.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceimpl implements StatisticsService {
    private final BankAccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;

    public StatisticsServiceimpl(BankAccountRepository accountRepository,
                              CategoryRepository categoryRepository,
                              OperationRepository operationRepository){
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public BigDecimal getNetProfit(LocalDateTime start, LocalDateTime end) {
        return operationRepository.findAll().stream()
                .filter(op -> !op.getDate().isBefore(start) && !op.getDate().isAfter(end))
                .map(op -> op.getOperationType() == OperationType.INCOME ? op.getAmount() : op.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //Принимаем даты фильтра проводим через НЕ раньше начала и НЕ после конца
        //Преобразуем amount в + и - относительно типа операции и складываем
    }

    @Override
    public Map<String, BigDecimal> getExpensesByCategory(LocalDateTime start, LocalDateTime end) {
        return operationRepository.findAll().stream()
                .filter(op -> op.getOperationType() == OperationType.EXPENSE)
                .filter(op -> !op.getDate().isBefore(start) && !op.getDate().isAfter(end))
                .collect(Collectors.groupingBy(
                        op -> categoryRepository.findById(op.getCategoryId())
                                .map(cat -> cat.getName())
                                .orElse("Неизвестная категория"),
                        Collectors.reducing(BigDecimal.ZERO, Operation::getAmount, BigDecimal::add)
                ));

        //внутри группировки принимаем id из репо категорий через интерфейс
    }


    @Override
    public Map<String, BigDecimal> getIncomesByCategory(LocalDateTime start, LocalDateTime end) {
        return operationRepository.findAll().stream()
                .filter(op -> op.getOperationType() == OperationType.INCOME)
                .filter(op -> !op.getDate().isBefore(start) && !op.getDate().isAfter(end))
                .collect(Collectors.groupingBy(
                        op -> categoryRepository.findById(op.getCategoryId())
                                .map(cat -> cat.getName())
                                .orElse("Неизвестная категория"),
                        Collectors.reducing(BigDecimal.ZERO, Operation::getAmount, BigDecimal::add)
                ));

    }
}
