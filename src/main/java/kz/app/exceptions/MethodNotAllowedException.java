package kz.app.exceptions;

public class MethodNotAllowedException extends RuntimeException {
    private final String msg;

    public MethodNotAllowedException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
