package com.t2.t2.Exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    // Constructor that accepts a message
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
