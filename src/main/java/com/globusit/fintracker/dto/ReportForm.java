package com.globusit.fintracker.dto;

import com.globusit.fintracker.domain.PeriodType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ReportForm {

    @NotNull
    private PeriodType periodType = PeriodType.MONTH;

    @NotNull
    @Min(2000)
    @Max(2100)
    private Integer year;

    @Min(1)
    @Max(12)
    private Integer month;

    @Min(1)
    @Max(4)
    private Integer quarter;
}
