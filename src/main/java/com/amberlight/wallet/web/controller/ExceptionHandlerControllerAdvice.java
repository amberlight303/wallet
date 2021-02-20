package com.amberlight.wallet.web.controller;


import com.amberlight.wallet.model.dto.ErrorDto;
import com.amberlight.wallet.model.exception.BusinessLogicException;
import com.amberlight.wallet.model.exception.ServerException;
import com.amberlight.wallet.util.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


/**
 * An exception handler.
 */
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    private static final Logger log = LogManager.getLogger(ExceptionHandlerControllerAdvice.class);

    private final HttpHeaders defaultHeaders = new HttpHeaders() {{
        add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    }};

    // 400

    @ExceptionHandler(value = BindException.class)
    protected ResponseEntity<ErrorDto> handleBindException(BindException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        final BindingResult result = ex.getBindingResult();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(defaultHeaders)
                .body(new ErrorDto(result.getAllErrors(),
                      String.format("Wrong binding. Invalid %s.", result.getObjectName())));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                             WebRequest request) {
        log.error(ex.getMessage(), ex);
        final BindingResult result = ex.getBindingResult();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(defaultHeaders)
                .body(new ErrorDto(result.getAllErrors(),
                      String.format("Method argument is not valid. Invalid %s.", result.getObjectName())));
    }


    // 450

    @ExceptionHandler(value = BusinessLogicException.class)
    protected ResponseEntity<ErrorDto> handleBusinessLogicException(BusinessLogicException ex, WebRequest request) {
        log.error("EXCODE: {}, MSG: {}", ex.getCode(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpUtil.HTTP_STATUS_BUSINESS_LOGIC_ERROR).headers(defaultHeaders)
                .body(new ErrorDto(ex.getMessage(), ex.getCode()));
    }

    // 500

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<ErrorDto> handleException(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(defaultHeaders)
                .body(new ErrorDto("Oops, something went wrong."));
    }

    @ExceptionHandler(value = ServerException.class)
    protected ResponseEntity<ErrorDto> handleServerException(ServerException ex, WebRequest request) {
        log.error("EXCODE: {}, MSG: {}", ex.getCode(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(defaultHeaders)
                .body(new ErrorDto(ex.getMessage(), ex.getCode()));
    }

}
