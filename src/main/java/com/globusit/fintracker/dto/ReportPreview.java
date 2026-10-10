package com.globusit.fintracker.dto;

import com.globusit.fintracker.domain.PeriodType;
import com.globusit.fintracker.domain.Transaction;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class ReportPreview {

    PeriodType periodType;
    LocalDate periodStart;
    LocalDate periodEnd;
    BigDecimal totalIncome;
    BigDecimal totalExpense;
    BigDecimal balance;
    List<Transaction> transactions;
}
