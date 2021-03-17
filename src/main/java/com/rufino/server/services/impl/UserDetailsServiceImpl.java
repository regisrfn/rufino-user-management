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

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            LOGGER.error(String.format("User %s not found", username));
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }

        user.setLastLoginDisplay(user.getLastLogin());
        user.setLastLogin(ZonedDateTime.now(ZoneId.of("Z")));
        userRepository.saveOrUpdateUser(user);
        return new UserSecurity(user);
    }

}
