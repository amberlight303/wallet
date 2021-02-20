package com.amberlight.wallet.model.exception;

/**
 * The expected exception caused by business logic.
 */
public class BusinessLogicException extends RuntimeException {

    private Integer code;

    public BusinessLogicException() {
        super();
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(Throwable cause) {
        super(cause);
    }

    public BusinessLogicException(String errorMessage, Integer code) {
        super(errorMessage);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
