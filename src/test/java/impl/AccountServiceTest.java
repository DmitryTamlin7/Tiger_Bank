package impl;

import org.example.domain.BankAccount;
import org.example.domain.Category;
import org.example.domain.OperationType;
import org.example.repository.BankAccountRepository;
import org.example.repository.CategoryRepository;
import org.example.repository.OperationRepository;
import org.example.service.imp.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private BankAccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешное пополнение счета (Income)")
    void createOperation_Success_Income() {
        Long accId = 1L;
        Long catId = 2L;
        BankAccount account = new BankAccount(accId, "Test Account", new BigDecimal("100.00"));
        Category category = new Category(catId, OperationType.INCOME, "Salary");

        when(accountRepository.findById(accId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(category));
        accountService.createOperation(accId, catId, new BigDecimal("50.00"), "Bonus");


        assertEquals(new BigDecimal("150.00"), account.getBalance());
        verify(accountRepository, times(1)).save(account);
        verify(operationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Ошибка при недостаточном балансе (Expense)")
    void createOperation_ThrowsException_InsufficientFunds() {
        Long accId = 1L;
        Long catId = 2L;
        BankAccount account = new BankAccount(accId, "Test Account", new BigDecimal("30.00"));
        Category category = new Category(catId, OperationType.EXPENSE, "Food");

        when(accountRepository.findById(accId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(category));

        assertThrows(RuntimeException.class, () ->
                accountService.createOperation(accId, catId, new BigDecimal("100.00"), "Expensive Dinner")
        );

        assertEquals(new BigDecimal("30.00"), account.getBalance());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка при создании операции с отрицательной суммой")
    void createOperation_ThrowsException_NegativeAmount() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                accountService.createOperation(1L, 1L, new BigDecimal("-10.00"), "Error")
        );

        assertTrue(exception.getMessage().contains("больше нуля"));
    }
}