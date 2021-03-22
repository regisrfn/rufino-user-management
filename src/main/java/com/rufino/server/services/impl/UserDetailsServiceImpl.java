package com.rufino.server.services.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.rufino.server.model.User;
import com.rufino.server.repository.UserRepository;
import com.rufino.server.security.UserSecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private LoginAttemptServiceImpl loginAttemptService;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, LoginAttemptServiceImpl loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            LOGGER.error(String.format("User %s not found", username));
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }

        validateLoginAndUnlock(user);
        user.setLastLoginDisplay(user.getLastLogin());
        user.setLastLogin(ZonedDateTime.now(ZoneId.of("Z")));
        userRepository.saveOrUpdateUser(user);
        return new UserSecurity(user);
    }

    private void validateLoginAndUnlock(User user) {
        if (!user.isLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername()))
                user.setLocked(true);
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

}
