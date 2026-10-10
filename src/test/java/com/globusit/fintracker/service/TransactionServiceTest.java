package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.Category;
import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.TransactionForm;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category incomeCategory;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("demo@globusit.local");
        incomeCategory = new Category();
        incomeCategory.setId(10L);
        incomeCategory.setName("Зарплата");
        incomeCategory.setType(OperationType.INCOME);
    }

    @Test
    void saveRejectsCategoryTypeMismatch() {
        TransactionForm form = new TransactionForm();
        form.setAmount(new BigDecimal("100"));
        form.setType(OperationType.EXPENSE);
        form.setCategoryId(10L);
        form.setDate(LocalDate.now());
        when(categoryService.getById(10L)).thenReturn(incomeCategory);

        assertThatThrownBy(() -> transactionService.save(form, user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("не совпадает");
    }

    @Test
    void savePersistsValidTransaction() {
        TransactionForm form = new TransactionForm();
        form.setAmount(new BigDecimal("1500.50"));
        form.setType(OperationType.INCOME);
        form.setCategoryId(10L);
        form.setDate(LocalDate.of(2026, 10, 1));
        form.setDescription("Аванс");
        when(categoryService.getById(10L)).thenReturn(incomeCategory);
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.save(form, user);

        ArgumentCaptor<com.globusit.fintracker.domain.Transaction> captor =
                ArgumentCaptor.forClass(com.globusit.fintracker.domain.Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo("1500.50");
        assertThat(captor.getValue().getUser()).isEqualTo(user);
        assertThat(captor.getValue().getCategory()).isEqualTo(incomeCategory);
    }
}
