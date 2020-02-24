package com.jambit.hlerchl.jambel.exceptions;

public class JambelResponseException extends JambelException {
    public JambelResponseException(String message) {
        super(message);
    }

    public JambelResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
