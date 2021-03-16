package com.rufino.server.filter;

import static com.rufino.server.constant.SecurityConst.OPTIONS_HTTP_METHOD;
import static com.rufino.server.constant.SecurityConst.TOKEN_PREFIX;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rufino.server.services.JwtTokenService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private JwtTokenService jwtTokenService;

    public JwtAuthorizationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String username = jwtTokenService.getSubject(token);

            if (jwtTokenService.isTokenValid(username, token)) {
                List<GrantedAuthority> authoritiesList = jwtTokenService.getAuthorities(token);
                Authentication authUser = jwtTokenService.getAuthentication(username, authoritiesList, request);
                SecurityContextHolder.getContext().setAuthentication(authUser);
            } else {
                SecurityContextHolder.clearContext();
            }
        }

    }

}
