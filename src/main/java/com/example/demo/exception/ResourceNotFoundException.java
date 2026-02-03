package com.example.demo.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(final String resource, final String id) {
        super("%s not found with id: %s".formatted(resource, id));
    }
}