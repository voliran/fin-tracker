package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.Category;
import com.globusit.fintracker.domain.Transaction;
import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.TransactionForm;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.exception.ResourceNotFoundException;
import com.globusit.fintracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public List<Transaction> findAllForUser(User user) {
        return transactionRepository.findByUserOrderByDateDescIdDesc(user);
    }

    public List<Transaction> findForPeriod(User user, LocalDate start, LocalDate end) {
        return transactionRepository.findWithCategoryForPeriod(user, start, end);
    }

    public Transaction getById(Long id, User user) {
        return transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Транзакция не найдена"));
    }

    @Transactional
    public Transaction save(TransactionForm form, User user) {
        Category category = categoryService.getById(form.getCategoryId());
        if (category.getType() != form.getType()) {
            throw new BusinessException("Тип операции не совпадает с типом категории");
        }
        Transaction transaction = form.getId() == null ? new Transaction() : getById(form.getId(), user);
        transaction.setAmount(form.getAmount());
        transaction.setType(form.getType());
        transaction.setCategory(category);
        transaction.setDate(form.getDate());
        transaction.setDescription(form.getDescription());
        transaction.setUser(user);
        Transaction saved = transactionRepository.save(transaction);
        log.info("Сохранена транзакция id={}, userId={}, amount={}", saved.getId(), user.getId(), saved.getAmount());
        return saved;
    }

    @Transactional
    public void delete(Long id, User user) {
        Transaction transaction = getById(id, user);
        transactionRepository.delete(transaction);
        log.info("Удалена транзакция id={}, userId={}", id, user.getId());
    }

    public TransactionForm toForm(Transaction transaction) {
        TransactionForm form = new TransactionForm();
        form.setId(transaction.getId());
        form.setAmount(transaction.getAmount());
        form.setType(transaction.getType());
        form.setCategoryId(transaction.getCategory().getId());
        form.setDate(transaction.getDate());
        form.setDescription(transaction.getDescription());
        return form;
    }
}
