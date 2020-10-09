package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Report {
    private GenericTable footer;
    private GenericTable header;
    private List<GenericContent> content;
    private List<String> parameters;
    private Metadata metadata;
}
