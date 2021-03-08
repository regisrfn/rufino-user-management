package com.rufino.server.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rufino.server.domain.HttpResponse;
import com.rufino.server.security.ConstantSecurity;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
            throws IOException {

        HttpResponse httpResponse = new HttpResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN,
                HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase(), ConstantSecurity.FORBIDDEN_MESSAGE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper om = new ObjectMapper();
        om.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }

}