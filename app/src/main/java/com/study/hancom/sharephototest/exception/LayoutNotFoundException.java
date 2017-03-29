package com.study.hancom.sharephototest.exception;

import java.io.FileNotFoundException;

public class LayoutNotFoundException extends FileNotFoundException {
    public LayoutNotFoundException() {
        super();
    }
    public LayoutNotFoundException(String message) {
        super(message);
    }
}