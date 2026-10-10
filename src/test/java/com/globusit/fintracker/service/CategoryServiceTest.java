package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.dto.CategoryForm;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    void saveRejectsDuplicate() {
        CategoryForm form = new CategoryForm();
        form.setName("Продукты");
        form.setType(OperationType.EXPENSE);
        when(categoryRepository.existsByNameIgnoreCaseAndType("Продукты", OperationType.EXPENSE)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.save(form))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("уже существует");
    }
}
