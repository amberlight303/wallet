package com.amberlight.wallet.model.exception;

/**
 * The expected exception caused by server actions not related to business logic.
 */
public class ServerException extends RuntimeException {

    private Integer code;

    public ServerException() {
        super();
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(String errorMessage, Integer code) {
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
