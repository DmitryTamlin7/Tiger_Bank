package org.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface StatisticsService {

    BigDecimal getNetProfit(LocalDateTime start, LocalDateTime end);

    Map<String, BigDecimal> getExpensesByCategory(LocalDateTime start, LocalDateTime end);
    Map<String, BigDecimal> getIncomesByCategory(LocalDateTime start, LocalDateTime end);
}
