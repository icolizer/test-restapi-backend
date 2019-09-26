package kz.app.exceptions;

import kz.app.validators.BrokenRule;

public class BadRequestException extends RuntimeException {
    private final String msg;

    public BadRequestException(String msg) {
        this.msg = msg;
    }

    public BadRequestException(BrokenRule brokenRule) {
        this.msg = brokenRule.getMessage();
    }

    public String getMsg() {
        return msg;
    }
}
