package com.rufino.server.exception;

import org.springframework.http.HttpStatus;

public class ApiRequestException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    

    public ApiRequestException(String message) {
        super(message);
    }

    public ApiRequestException(String message, HttpStatus httpStatus) {
        super(message);
        setHttpStatus(httpStatus);        
    }


    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}