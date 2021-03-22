package com.rufino.server.services;

public interface LoginAttemptService {
    public void evictUserFromLoginAttemptCache(String username);
    public void addUserToLoginAttemptCache(String username);
    public boolean hasExceededMaxAttempts(String username);
    public void clearAll();
}
