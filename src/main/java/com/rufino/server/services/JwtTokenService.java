package com.rufino.server.services;

import static com.rufino.server.security.ConstantSecurity.AUTHORITIES;
import static com.rufino.server.security.ConstantSecurity.EXPIRATION_TIME;
import static com.rufino.server.security.ConstantSecurity.RUFINO_LLC;
import static com.rufino.server.security.ConstantSecurity.TOKEN_CANNOT_BE_VERIFIED;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.security.UserSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class JwtTokenService {

    private Dotenv dotenv;
    private String jwtSecret;
    private Algorithm algorithm;

    @Autowired
    public JwtTokenService(Dotenv dotenv) {
        this.dotenv = dotenv;
        this.jwtSecret = this.dotenv.get("JWT_SECRET");
        this.algorithm = Algorithm.HMAC256(jwtSecret);
    }

    public String generateToken(UserSecurity userSecurity) {
        String[] claims = getClaimsFromUser(userSecurity);
        Date currentDate = new Date();
        return JWT.create().withArrayClaim(AUTHORITIES, claims).withIssuedAt(currentDate).withIssuer(RUFINO_LLC)
                .withSubject(userSecurity.getUsername())
                .withExpiresAt(new Date(currentDate.getTime() + EXPIRATION_TIME)).sign(this.algorithm);
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        List<String> claims = getClaimsFromToken(token);
        return claims.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    }

    private List<String> getClaimsFromToken(String token) {
        try {
            JWTVerifier verifier = getJwtVerifier();
            return verifier.verify(token).getClaim(AUTHORITIES).asList(String.class);
        } catch (JWTVerificationException e) {
            throw new ApiRequestException(TOKEN_CANNOT_BE_VERIFIED, HttpStatus.FORBIDDEN);
        }

    }

    private JWTVerifier getJwtVerifier() {
        return JWT.require(this.algorithm).withIssuer(RUFINO_LLC).build();
    }

    private String[] getClaimsFromUser(UserSecurity userSecurity) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : userSecurity.getAuthorities())
            authorities.add(grantedAuthority.getAuthority());
        return authorities.toArray(new String[0]);
    }

}
