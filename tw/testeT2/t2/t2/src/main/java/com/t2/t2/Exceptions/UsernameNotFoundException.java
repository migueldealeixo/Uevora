package com.t2.t2.Exceptions;

public class UsernameNotFoundException extends RuntimeException {

    // Constructor that accepts a message
    public UsernameNotFoundException(String message) {
        // Pass the message to the RuntimeException constructor
        super(message);
    }
}
