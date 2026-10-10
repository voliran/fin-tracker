package com.globusit.fintracker.config;

import com.globusit.fintracker.domain.Category;
import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.domain.Transaction;
import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.repository.CategoryRepository;
import com.globusit.fintracker.repository.TransactionRepository;
import com.globusit.fintracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedDemoData() {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            User demo = new User();
            demo.setName("Демо пользователь");
            demo.setEmail("demo@globusit.local");
            demo.setPassword(passwordEncoder.encode("demo123"));
            userRepository.save(demo);

            Category salary = category("Зарплата", OperationType.INCOME);
            Category freelance = category("Фриланс", OperationType.INCOME);
            Category food = category("Продукты", OperationType.EXPENSE);
            Category transport = category("Транспорт", OperationType.EXPENSE);
            Category rent = category("Жильё", OperationType.EXPENSE);
            category("Развлечения", OperationType.EXPENSE);

            LocalDate today = LocalDate.now();
            saveTx(demo, salary, OperationType.INCOME, "120000.00", today.withDayOfMonth(1), "Оклад");
            saveTx(demo, freelance, OperationType.INCOME, "35000.00", today.minusDays(5), "Проект");
            saveTx(demo, food, OperationType.EXPENSE, "12450.50", today.minusDays(2), "Магазин");
            saveTx(demo, transport, OperationType.EXPENSE, "3200.00", today.minusDays(3), "Проездной");
            saveTx(demo, rent, OperationType.EXPENSE, "45000.00", today.withDayOfMonth(5), "Аренда");
            log.info("Создан демо-пользователь demo@globusit.local / demo123");
        };
    }

    private Category category(String name, OperationType type) {
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        return categoryRepository.save(category);
    }

    private void saveTx(User user, Category category, OperationType type, String amount, LocalDate date, String description) {
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setCategory(category);
        tx.setType(type);
        tx.setAmount(new BigDecimal(amount));
        tx.setDate(date);
        tx.setDescription(description);
        transactionRepository.save(tx);
    }
}
