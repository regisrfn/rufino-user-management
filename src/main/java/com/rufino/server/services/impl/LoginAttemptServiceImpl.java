package com.rufino.server.services.impl;

import static com.rufino.server.constant.ExceptionConst.LOGIN_ATTEMPT_ERROR_MSG;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.services.LoginAttemptService;

public class LoginAttemptServiceImpl implements LoginAttemptService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptServiceImpl() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });

    }

    @Override
    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);

    }

    @Override
    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts += loginAttemptCache.get(username);
            loginAttemptCache.put(username, attempts);
        } catch (ExecutionException e) {
            throw new ApiRequestException(LOGIN_ATTEMPT_ERROR_MSG, INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            throw new ApiRequestException(LOGIN_ATTEMPT_ERROR_MSG, INTERNAL_SERVER_ERROR);
        }
    }

}
