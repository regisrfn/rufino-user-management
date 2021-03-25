package com.rufino.server.services.impl;

import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;

import java.util.List;

import com.rufino.server.model.User;
import com.rufino.server.repository.UserRepository;
import com.rufino.server.security.UserSecurity;
import com.rufino.server.services.EmailService;
import com.rufino.server.services.JwtTokenService;
import com.rufino.server.services.UserService;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final int DEFAULT_PASSWORD_LENGTH = 12;

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenService jwtTokenService;
    private AuthenticationManager authenticationManager;
    private  EmailService emailService;

    @Autowired
    public UserServiceImpl( UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            JwtTokenService jwtTokenService, 
                            AuthenticationManager authenticationManager,
                            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    @Override
    public User register(User user) {
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        User savedUser = userRepository.saveOrUpdateUser(user);
        emailService.sendEmail(user.getFirstName(), password, user.getEmail());
        return savedUser;
    }

    @Override
    public List<User> getUsers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseEntity<User> login(User user) {
        try {
            authenticate(user.getUsername(), user.getPassword());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException(null);
        }
        User validUser = userRepository.getUserByUsername(user.getUsername());
        UserSecurity userSecurity = new UserSecurity(validUser);
        HttpHeaders jwtHeaders = getJwtHeader(userSecurity);
        return new ResponseEntity<>(validUser, jwtHeaders, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(UserSecurity userSecurity) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenService.generateToken(userSecurity));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword(){
        return RandomStringUtils.randomAlphanumeric(DEFAULT_PASSWORD_LENGTH);
    }
}
