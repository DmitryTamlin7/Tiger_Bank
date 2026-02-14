package domain;

import org.example.domain.BankAccount;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTest {

    @Test
    void TestAccountCreation(){
        BankAccount bank = new BankAccount(1L, "Зарплатный счет", new BigDecimal(100));

        assertEquals("Зарплатный счет", bank.getName());
        assertEquals(new BigDecimal(100), bank.getBalance());
    }
}
