package org.example.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Category {
    private Long id;
    private OperationType operationType;
    private String name;
}
