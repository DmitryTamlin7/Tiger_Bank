package repository;


import org.example.domain.BankAccount;
import org.example.repository.BankAccountRepository;
import org.example.repository.impl.InMemoryBankAccountRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;



public class InMemoryBankAccountRepositoryTest {

    @Test
    void shouldSaveAndFindAccount()
    {
        BankAccountRepository repository = new InMemoryBankAccountRepository();
        BankAccount bankAccount = new BankAccount(null, "Зарплатный счет", new BigDecimal(1000));

        BankAccount saved = repository.save(bankAccount);

        assertNotNull(saved.getId());
        assertEquals("Зарплатный счет", repository.findById(saved.getId()).get().getName());
    }

}
