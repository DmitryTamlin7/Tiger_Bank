package org.example.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Operation {
    private Long id;
    private Long bankAccountId;
    private Long categoryId;
    private OperationType operationType;
    private BigDecimal amount;
    private LocalDateTime date;
    private String description;
}
