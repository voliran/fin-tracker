package com.globusit.fintracker.dto;

import com.globusit.fintracker.domain.OperationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TransactionForm {

    private Long id;

    @NotNull(message = "Укажите сумму")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;

    @NotNull(message = "Выберите тип операции")
    private OperationType type;

    @NotNull(message = "Выберите категорию")
    private Long categoryId;

    @NotNull(message = "Укажите дату")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date = LocalDate.now();

    @Size(max = 255, message = "Описание не длиннее 255 символов")
    private String description;
}
