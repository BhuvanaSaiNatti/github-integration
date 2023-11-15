package com.redcarepharmacy.exception;

public class GithubException extends RuntimeException {

    private final int errorCode;

    public GithubException(final int errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
