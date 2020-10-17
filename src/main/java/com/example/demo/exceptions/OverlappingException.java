package com.example.demo.exceptions;

public class OverlappingException extends Exception {
    public OverlappingException(String message) {
        super(message);
    }
    public OverlappingException(Exception exception) {
        super(exception);
    }
}
