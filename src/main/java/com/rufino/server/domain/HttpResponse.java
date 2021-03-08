package com.rufino.server.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpResponse {
    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String message, reason;

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {

        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.message = message;
        this.reason = reason;
    }

    public HttpResponse() {
    }

}
