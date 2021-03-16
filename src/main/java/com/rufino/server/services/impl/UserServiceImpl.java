package com.rufino.server.services.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.rufino.server.model.User;
import com.rufino.server.repository.UserRepository;
import com.rufino.server.security.UserSecurity;
import com.rufino.server.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByNickname(username);

        if (user == null) {
            LOGGER.error(String.format("User %s not found", username));
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }

        user.setLastLoginDisplay(user.getLastLogin());
        user.setLastLogin(ZonedDateTime.now(ZoneId.of("Z")));
        userRepository.saveOrUpdateUser(user);
        return new UserSecurity(user);
    }

    @Override
    public User register(User user) {
        String password = user.getPassword();
        user.setPassword(encodePassword(password));
        return userRepository.saveOrUpdateUser(user);
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

    private String encodePassword(String password) {
        return null;
    }

}
