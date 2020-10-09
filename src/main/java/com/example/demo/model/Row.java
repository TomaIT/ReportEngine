package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Row {
    private List<Column> cols;
}
