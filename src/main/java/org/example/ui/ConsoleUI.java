package org.example.ui;

import org.example.domain.OperationType;
import org.example.repository.BankAccountRepository;
import org.example.repository.CategoryRepository;
import org.example.repository.OperationRepository;
import org.example.service.AccountService;
import org.example.service.FileService;
import org.example.service.StatisticsService;
import org.example.domain.BankAccount;
import org.example.domain.Category;
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
    private final OperationRepository operationRepository;
    private  final FileService fileService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(BankAccountRepository accountRepository,
                     AccountService accountService,
                     StatisticsService statisticsService,
                     CategoryRepository categoryRepository,
                     OperationRepository operationRepository,
                     FileService fileService) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.statisticsService = statisticsService;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
        this.fileService = fileService;
    }

    @Override
    public void run(String... args) {
        System.out.println("* ДОБРО ПОЖАЛОВАТЬ В ТИГР-БАНК     ");
        System.out.println("* Ваш капитал под защитой! 🐯  ");


        while (true) {
            System.out.println("\n--- ГЛАВНОЕ МЕНЮ ---");
            System.out.println("1. 🏦 Управление счетами");
            System.out.println("2. 🏷  Управление категориями");
            System.out.println("3. 💸 Операции (Доходы/Расходы)");
            System.out.println("4. 📊 Аналитика и Отчеты");
            System.out.println("5. ⚙️  Сервис (Пересчет баланса)");
            System.out.println("6. 💾 Файлы (Импорт/Экспорт)");
            System.out.println("0. 🚪 Выход");
            System.out.print("\n🐯 Ваш выбор: ");

            int choice = readInt();
            try {
                switch (choice) {
                    case 1 -> manageAccountsMenu();
                    case 2 -> manageCategoriesMenu();
                    case 3 -> manageOperationsMenu();
                    case 4 -> showStatisticsMenu();
                    case 5 -> manualRecalculate();
                    case 6 -> manageFilesMenu();
                    case 0 -> {
                        System.out.println("\n🐯 Спасибо, что выбрали Тигр-Банк! До встречи.");
                        return;
                    }
                    default -> System.out.println("⚠️ Нет такого пункта, попробуйте еще раз.");
                }
            } catch (Exception e) {
                System.out.println("❌ Ошибка выполнения: " + e.getMessage());
            }
        }
    }

    private void manageAccountsMenu() {
        System.out.println("\n--- УПРАВЛЕНИЕ СЧЕТАМИ ---");
        System.out.println("1. Список счетов");
        System.out.println("2. Открыть новый счет");
        System.out.println("3. Изменить название счета");
        System.out.println("4. Закрыть счет (Удалить)");
        System.out.println("0. Назад");

        int choice = readInt();
        switch (choice) {
            case 1 -> showAccounts();
            case 2 -> createAccount();
            case 3 -> updateAccount();
            case 4 -> deleteAccount();
        }
    }

    private void manageCategoriesMenu() {
        System.out.println("\n--- УПРАВЛЕНИЕ КАТЕГОРИЯМИ ---");
        System.out.println("1. Список категорий");
        System.out.println("2. Создать категорию");
        System.out.println("3. Изменить название категории");
        System.out.println("4. Удалить категорию");
        System.out.println("0. Назад");

        int choice = readInt();
        switch (choice) {
            case 1 -> showCategories();
            case 2 -> createCategory();
            case 3 -> {
                showCategories();
                System.out.print("Введите ID категории для переименования: ");
                Long id = readLong();
                System.out.print("Новое название: ");
                accountService.updateCategoryName(id, readString());
                System.out.println("✅ Категория обновлена!");
            }
            case 4 -> {
                showCategories();
                System.out.print("Введите ID категории для удаления: ");
                accountService.deleteCategory(readLong());
                System.out.println("✅ Категория удалена.");
            }
        }
    }

    private void manageFilesMenu() {
        System.out.println("\n--- РАБОТА С ФАЙЛАМИ ---");
        System.out.println("1. Сохранить все в JSON");
        System.out.println("2. Загрузить из JSON");
        System.out.println("3. Сохранить все в YAML");
        System.out.println("4. Загрузить из YAML");
        System.out.println("5. Сохранить отчеты в CSV (Excel)");
        System.out.println("6. Загрузить из CSV");
        System.out.println("0. Назад");

        int choice = readInt();
        try {
            switch (choice) {
                case 1 -> fileService.exportData("json");
                case 2 -> fileService.importData("json");
                case 3 -> fileService.exportData("yaml");
                case 4 -> fileService.importData("yaml");
                case 5 -> fileService.exportData("csv");
                case 6 -> fileService.importData("csv");
            }
        } catch (Exception e) {
            System.out.println("❌ Ошибка при работе с файлом: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void manageOperationsMenu() {
        System.out.println("\n--- ОПЕРАЦИИ ---");
        System.out.println("1. Провести операцию (Расход/Доход)");
        System.out.println("2. Удалить операцию (по ID из истории)");
        System.out.println("3. Посмотреть историю операций");
        System.out.println("0. Назад");

        int choice = readInt();
        switch (choice) {
            case 1 -> addOperation();
            case 2 -> {
                showHistory();
                System.out.print("\nВведите ID операции для удаления: ");
                accountService.deleteOperation(readLong());
                System.out.println("✅ Операция отменена. Баланс пересчитан автоматически.");
            }
            case 3 -> showHistory();
        }
    }



    private void showStatisticsMenu() {
        System.out.println("\n--- АНАЛИТИКА ТИГР-БАНКА ---");
        BigDecimal netProfit = statisticsService.getNetProfit(LocalDateTime.now().minusMonths(1), LocalDateTime.now());
        System.out.println("💰 Чистая прибыль за месяц: " + netProfit);

        System.out.println("\n📉 Расходы по категориям:");
        statisticsService.getExpensesByCategory(LocalDateTime.now().minusMonths(1), LocalDateTime.now())
                .forEach((cat, sum) -> System.out.println(" • " + cat + ": " + sum));
    }


    private void showAccounts() {
        System.out.println("\n--- ВАШИ СЧЕТА ---");
        if (accountRepository.findAll().isEmpty()) System.out.println("У вас пока нет открытых счетов.");
        accountRepository.findAll().forEach(a ->
                System.out.println("ID: " + a.getId() + " | " + a.getName() + " | Баланс: " + a.getBalance()));
    }

    private void showCategories() {
        System.out.println("\n--- ДОСТУПНЫЕ КАТЕГОРИИ ---");
        categoryRepository.findAll().forEach(c -> {
            String type = (c.getOperationType() == OperationType.INCOME) ? "Доход" : "Расход";
            System.out.println(c.getId() + ". " + c.getName() + " [" + type + "]");
        });
    }

    private void showHistory() {
        System.out.println("\n--- ИСТОРИЯ ОПЕРАЦИЙ ---");
        if (operationRepository.findAll().isEmpty()) System.out.println("История пуста.");
        operationRepository.findAll().forEach(op ->
                System.out.println("ID: " + op.getId() + " | Сумма: " + op.getAmount() + " | Описание: " + op.getDescription()));
    }

    private void createAccount() {
        System.out.print("Название счета: ");
        String name = readString();
        System.out.print("Начальный баланс: ");
        BigDecimal balance = readBigDecimal();
        accountRepository.save(new BankAccount(null, name, balance));
        System.out.println("✅ Счет успешно создан!");
    }

    private void updateAccount() {
        showAccounts();
        System.out.print("ID счета для переименования: ");
        Long id = readLong();
        System.out.print("Новое имя счета: ");
        accountService.updateAccountName(id, readString());
        System.out.println("✅ Название изменено!");
    }

    private void deleteAccount() {
        showAccounts();
        System.out.print("ID счета для удаления: ");
        Long id = readLong();
        accountRepository.deleteById(id);
        System.out.println("✅ Счет удален.");
    }

    private void createCategory() {
        System.out.print("Название категории: ");
        String name = readString();
        System.out.print("Тип (1 - Доход, 2 - Расход): ");
        OperationType type = (readInt() == 1) ? OperationType.INCOME : OperationType.EXPENSE;
        categoryRepository.save(new Category(null, type, name));
        System.out.println("✅ Категория создана.");
    }

    private void addOperation() {
        showAccounts();
        System.out.print("Введите ID счета: ");
        Long accId = readLong();
        showCategories();
        System.out.print("Введите ID категории: ");
        Long catId = readLong();
        System.out.print("Сумма: ");
        BigDecimal amount = readBigDecimal();
        System.out.print("Комментарий: ");
        accountService.createOperation(accId, catId, amount, readString());
        System.out.println("✅ Операция проведена!");
    }

    private void manualRecalculate() {
        showAccounts();
        System.out.print("Введите ID счета для глубокой проверки баланса: ");
        accountService.recalculateBalance(readLong());
        System.out.println("✅ Баланс синхронизирован с историей операций.");
    }

    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("⚠️ Введите число: ");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    private Long readLong() {
        while (!scanner.hasNextLong()) {
            System.out.print("⚠️ Введите корректный ID: ");
            scanner.next();
        }
        Long val = scanner.nextLong();
        scanner.nextLine();
        return val;
    }

    private BigDecimal readBigDecimal() {
        while (!scanner.hasNextBigDecimal()) {
            System.out.print("⚠️ Введите сумму (число): ");
            scanner.next();
        }
        BigDecimal val = scanner.nextBigDecimal();
        scanner.nextLine();
        return val;
    }

    private String readString() {
        return scanner.nextLine();
    }
}