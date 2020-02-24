package com.jambit.hlerchl.jambel.exceptions;

public class JambelException extends Exception {
    public JambelException(String message) {
        super(message);
    }

    public JambelException(String message, Throwable cause) {
        super(message, cause);
    }
}
