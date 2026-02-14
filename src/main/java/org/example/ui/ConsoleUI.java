package org.example.ui;

import org.example.domain.BankAccount;
import org.example.domain.Category;
import org.example.domain.OperationType;
import org.example.repository.BankAccountRepository;
import org.example.repository.CategoryRepository;
import org.example.service.AccountService;
import org.example.service.StatisticsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

@Component
public class ConsoleUI implements CommandLineRunner {

    private final AccountService accountService;
    private final StatisticsService statisticsService;
    private final BankAccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(BankAccountRepository accountRepository,
                     AccountService accountService,
                     StatisticsService statisticsService,
                     CategoryRepository categoryRepository) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.statisticsService = statisticsService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("Вас Приветствует ЗАО ТИГР-БАНК 🐯");

        while (true) {
            printMainMenu();
            int choice = readInt();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> createCategory();
                case 3 -> addOperation();
                case 4 -> showStatistics();
                case 5 -> showAllData();
                case 0 -> {
                    System.out.println("Выход... До свидания! 🐯");
                    return;
                }
                default -> System.out.println("Неверный ввод РРРР.. 🐯 ");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n--- ГЛАВНОЕ МЕНЮ ---");
        System.out.println("1. Создать счет");
        System.out.println("2. Создать категорию");
        System.out.println("3. Добавить операцию");
        System.out.println("4. Посмотреть статистику");
        System.out.println("5. Показать мои счета");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void createAccount() {
        System.out.print("Введите название счета: ");
        String name = scanner.next();
        System.out.print("Введите начальный баланс: ");
        BigDecimal balance = scanner.nextBigDecimal();

        BankAccount account = new BankAccount(null, name, balance);
        accountRepository.save(account);
        System.out.println("Счет создан успешно!");
    }

    private void createCategory() {
        System.out.print("Введите название категории: ");
        String name = scanner.next();
        System.out.print("Тип (1 - Доход, 2 - Расход): ");
        int typeChoice = readInt();
        OperationType type = (typeChoice == 1) ? OperationType.INCOME : OperationType.EXPENSE;

        Category category = new Category(null, type, name);
        categoryRepository.save(category);
        System.out.println("Категория создана!");
    }

    private void addOperation() {
        if (accountRepository.findAll().isEmpty() || categoryRepository.findAll().isEmpty()) {
            System.out.println("Ошибка: Сначала создайте счет и хотя бы одну категорию!");
            return;
        }

        System.out.println("Выберите ID счета:");
        accountRepository.findAll().forEach(a -> System.out.println(a.getId() + ": " + a.getName() + " (Баланс: " + a.getBalance() + ")"));
        Long accId = scanner.nextLong();

        System.out.println("Выберите ID категории:");
        categoryRepository.findAll().forEach(c -> System.out.println(c.getId() + ": " + c.getName() + " [" + c.getOperationType() + "]"));
        Long catId = scanner.nextLong();

        System.out.print("Введите сумму: ");
        BigDecimal amount = scanner.nextBigDecimal();
        System.out.print("Описание: ");
        String desc = scanner.next();

        try {
            accountService.createOperation(accId, catId, amount, desc);
            System.out.println("Операция успешно проведена!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showStatistics() {
        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);

        System.out.println("\n--- АНАЛИТИКА ---");
        System.out.println("Чистая прибыль за период: " + statisticsService.getNetProfit(start, end));
        System.out.println("Расходы по категориям: " + statisticsService.getExpensesByCategory(start, end));
    }

    private void showAllData() {
        System.out.println("\n--- ВАШИ СЧЕТА ---");
        accountRepository.findAll().forEach(a -> System.out.println(a.getId() + ". " + a.getName() + ": " + a.getBalance()));
    }

    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.println("Пожалуйста, введите число.");
            scanner.next();
        }
        int val = scanner.nextInt();
        return val;
    }
}