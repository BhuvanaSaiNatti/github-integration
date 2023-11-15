package com.redcarepharmacy.exception;

public class ApplicationException extends RuntimeException {
    private final int errorCode;

    public ApplicationException(int code, String msg) {
        super(msg);
        this.errorCode = code;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
