package com.globusit.fintracker.repository;

import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.dto.DashboardMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public BigDecimal sumByUserTypeAndPeriod(Long userId, OperationType type, LocalDate start, LocalDate end) {
        BigDecimal result = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = ? AND tx_date BETWEEN ? AND ?",
                BigDecimal.class,
                userId, type.name(), Date.valueOf(start), Date.valueOf(end)
        );
        return result == null ? BigDecimal.ZERO : result;
    }

    public long countByUserAndPeriod(Long userId, LocalDate start, LocalDate end) {
        Long result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND tx_date BETWEEN ? AND ?",
                Long.class,
                userId, Date.valueOf(start), Date.valueOf(end)
        );
        return result == null ? 0 : result;
    }

    public List<DashboardMetrics.NamedAmount> sumsByCategory(Long userId, OperationType type, LocalDate start, LocalDate end) {
        return jdbcTemplate.query(
                "SELECT c.name, COALESCE(SUM(t.amount), 0) AS total " +
                        "FROM transactions t JOIN categories c ON c.id = t.category_id " +
                        "WHERE t.user_id = ? AND t.type = ? AND t.tx_date BETWEEN ? AND ? " +
                        "GROUP BY c.name ORDER BY total DESC",
                (rs, rowNum) -> new DashboardMetrics.NamedAmount(rs.getString("name"), rs.getBigDecimal("total")),
                userId, type.name(), Date.valueOf(start), Date.valueOf(end)
        );
    }

    public List<DashboardMetrics.MonthlyPoint> monthlyTrend(Long userId, LocalDate start, LocalDate end) {
        return jdbcTemplate.query(
                "SELECT EXTRACT(YEAR FROM tx_date) AS y, EXTRACT(MONTH FROM tx_date) AS m, " +
                        "COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) AS income, " +
                        "COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) AS expense " +
                        "FROM transactions WHERE user_id = ? AND tx_date BETWEEN ? AND ? " +
                        "GROUP BY EXTRACT(YEAR FROM tx_date), EXTRACT(MONTH FROM tx_date) ORDER BY y, m",
                (rs, rowNum) -> {
                    int year = rs.getInt("y");
                    int month = rs.getInt("m");
                    String label = String.format("%04d-%02d", year, month);
                    return new DashboardMetrics.MonthlyPoint(label, rs.getBigDecimal("income"), rs.getBigDecimal("expense"));
                },
                userId, Date.valueOf(start), Date.valueOf(end)
        );
    }
}
