package com.hevy.demo.service.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    ResourceNotFoundException(String msg) {
        super(msg);
    }

}
