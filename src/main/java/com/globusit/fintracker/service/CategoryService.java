package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.Category;
import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.dto.CategoryForm;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.exception.ResourceNotFoundException;
import com.globusit.fintracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByTypeAscNameAsc();
    }

    public List<Category> findByType(OperationType type) {
        return categoryRepository.findByTypeOrderByNameAsc(type);
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена"));
    }

    @Transactional
    public Category save(CategoryForm form) {
        if (form.getId() == null && categoryRepository.existsByNameIgnoreCaseAndType(form.getName(), form.getType())) {
            throw new BusinessException("Такая категория уже существует");
        }
        Category category = form.getId() == null ? new Category() : getById(form.getId());
        category.setName(form.getName().trim());
        category.setType(form.getType());
        Category saved = categoryRepository.save(category);
        log.info("Сохранена категория id={}, name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Category category = getById(id);
        categoryRepository.delete(category);
        log.info("Удалена категория id={}", id);
    }
}
