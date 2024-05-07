package com.fastcampus.board.exception;

public class DuplicateIdException extends RuntimeException {
    public DuplicateIdException(String msg) {
        super(msg);
    }
}
