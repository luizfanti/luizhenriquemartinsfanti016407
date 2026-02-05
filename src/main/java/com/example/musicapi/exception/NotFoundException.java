package com.example.musicapi.exception;
import java.util.stream.Collectors;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
