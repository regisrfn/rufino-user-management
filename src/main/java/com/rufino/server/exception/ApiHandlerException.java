package com.rufino.server.exception;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.rufino.server.domain.HttpResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiHandlerException implements ErrorController {

    public static final String ERROR_PATH = "/error";

    @ExceptionHandler(value = { ApiRequestException.class })
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
        HttpStatus httpStatus = e.getHttpStatus();
        Map<String, String> errors = new HashMap<>();
        errors.put("apiError", e.getMessage());
        ApiException apiException = new ApiException(errors, httpStatus, ZonedDateTime.now());

        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiException apiException = new ApiException(errors, badRequest, ZonedDateTime.now());

        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleDBException(DataIntegrityViolationException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = handleSqlError(e);
        ApiException apiException = new ApiException(errors, badRequest, ZonedDateTime.now());

        return new ResponseEntity<>(apiException, badRequest);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return createHttpResponse(HttpStatus.NOT_FOUND, "There is no mapping for this URL");
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }

    private Map<String, String> handleSqlError(DataIntegrityViolationException e) {

        String errorMsg = e.getMessage();
        errorMsg = errorMsg.replace("\n", "").replace("\r", "");

        String pattern = ".*\\w*; SQL.*;.*\\[(uk_customer_email)\\].*";
        String error = (errorMsg.replaceAll(pattern, "$1"));
        Map<String, String> errors = new HashMap<>();

        if (error.equals("uk_customer_email"))
            errors.put("customerEmail", "Email not available");

        return errors;
    }

}