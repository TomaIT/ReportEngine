package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Condition {
    private String attribute;
    private String operator;
    private String value;
    private String gate;
}
