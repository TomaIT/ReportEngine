package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GenericTable {
    private List<Row> rows;
}
