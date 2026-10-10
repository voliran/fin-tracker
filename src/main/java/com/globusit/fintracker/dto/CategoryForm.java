package com.globusit.fintracker.dto;

import com.globusit.fintracker.domain.OperationType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CategoryForm {

    private Long id;

    @NotBlank(message = "Укажите название категории")
    @Size(max = 80, message = "Название не длиннее 80 символов")
    private String name;

    @NotNull(message = "Выберите тип категории")
    private OperationType type;
}
