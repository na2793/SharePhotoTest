package com.study.hancom.sharephototest.exception;

public class TooManyFailedAttemptsException extends Exception {
    public TooManyFailedAttemptsException() {
        super();
    }
    public TooManyFailedAttemptsException(String message) {
        super(message);
    }
}
