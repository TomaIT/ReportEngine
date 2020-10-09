package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GenericContent {
    private String id;
    private String label;
    private String type;
    private String mapping;
    private int maxrows;
    private List<Column> cols;
    private List<Condition> conditions;
    private String querysource;
}
