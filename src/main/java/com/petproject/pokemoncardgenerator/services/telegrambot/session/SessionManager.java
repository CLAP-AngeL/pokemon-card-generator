package com.petproject.pokemoncardgenerator.services.telegrambot.session;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    private final ConcurrentHashMap<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public UserSession getSession(Long userId) {
        return sessions.computeIfAbsent(userId, k -> new UserSession());
    }

    public void clearSession(Long userId) {
        sessions.remove(userId);
    }
}
