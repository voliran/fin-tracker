package com.globusit.fintracker.repository;

import com.globusit.fintracker.domain.Category;
import com.globusit.fintracker.domain.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByTypeAscNameAsc();

    List<Category> findByTypeOrderByNameAsc(OperationType type);

    boolean existsByNameIgnoreCaseAndType(String name, OperationType type);
}
