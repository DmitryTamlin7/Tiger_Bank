package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.domain.BankAccount;
import org.example.domain.BackupData;
import org.example.domain.Category;
import org.example.domain.Operation;
import org.example.repository.BankAccountRepository;
import org.example.repository.CategoryRepository;
import org.example.repository.OperationRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.io.IOException;

@Service
public class FileService {

    private final BankAccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;


    public FileService(BankAccountRepository accountRepository, CategoryRepository categoryRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    public void exportData(String format) throws IOException {
        BackupData data = new BackupData(
                accountRepository.findAll(),
                categoryRepository.findAll(),
                operationRepository.findAll()
        );

        switch (format.toLowerCase()){
            case "json" -> saveJson(data);
            case "yaml" -> saveYaml(data);
            case "csv" -> saveCsv(data);
            default -> throw new IllegalArgumentException("Неправильный аргумент " + format);
        }
    }

    public void importData(String format) throws  IOException{
        BackupData data = null;

        switch (format.toLowerCase()){
            case "json" -> data = loadJson();
            case "yaml" -> data = loadYaml();
            case "csv" -> data = loadCsv();
            default -> throw new IllegalArgumentException("Неправильный аргумент " + format);
        }
        if (data != null){

        }
    }

    private void saveJson(BackupData data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File("backup.json"), data);
        System.out.println("✅ Данные сохранены в backup.json");
    }

    private BackupData loadJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(new File("backup.json"), BackupData.class);
    }

    // === ЛОГИКА YAML ===
    private void saveYaml(BackupData data) throws IOException {
        ObjectMapper mapper = new YAMLMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.writeValue(new File("backup.yaml"), data);
        System.out.println("✅ Данные сохранены в backup.yaml");
    }

    private BackupData loadYaml() throws IOException {
        ObjectMapper mapper = new YAMLMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(new File("backup.yaml"), BackupData.class);
    }

    // === ЛОГИКА CSV (3 файла) ===
    private void saveCsv(BackupData data) throws IOException {
        CsvMapper mapper = new CsvMapper();
        mapper.registerModule(new JavaTimeModule());

        saveCsvList(mapper, BankAccount.class, data.getAccounts(), "backup_accounts.csv");
        saveCsvList(mapper, Category.class, data.getCategories(), "backup_categories.csv");
        saveCsvList(mapper, Operation.class, data.getOperations(), "backup_operations.csv");

        System.out.println("✅ Данные сохранены в 3 файла CSV (accounts, categories, operations)");
    }

    private <T> void saveCsvList(CsvMapper mapper, Class<T> clazz, List<T> list, String filename) throws IOException {
        CsvSchema schema = mapper.schemaFor(clazz).withHeader();
        mapper.writer(schema).writeValue(new File(filename), list);
    }

    private BackupData loadCsv() throws IOException {
        CsvMapper mapper = new CsvMapper();
        mapper.registerModule(new JavaTimeModule());

        List<BankAccount> accounts = loadCsvList(mapper, BankAccount.class, "backup_accounts.csv");
        List<Category> categories = loadCsvList(mapper, Category.class, "backup_categories.csv");
        List<Operation> operations = loadCsvList(mapper, Operation.class, "backup_operations.csv");

        return new BackupData(accounts, categories, operations);
    }

    private <T> List<T> loadCsvList(CsvMapper mapper, Class<T> clazz, String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) return List.of();
        CsvSchema schema = mapper.schemaFor(clazz).withHeader();
        return mapper.readerFor(clazz).with(schema).<T>readValues(file).readAll();
    }




    private void restoreData(BackupData data) {
        accountRepository.deleteAll();
        categoryRepository.deleteAll();
        operationRepository.deleteAll();


        if (data.getAccounts() != null) data.getAccounts().forEach(accountRepository::save);
        if (data.getCategories() != null) data.getCategories().forEach(categoryRepository::save);
        if (data.getOperations() != null) data.getOperations().forEach(operationRepository::save);

        System.out.println("✅ База данных успешно восстановлена из файла!");
    }
}
