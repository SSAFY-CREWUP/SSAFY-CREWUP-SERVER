package com.ssafy.crewup.global.config;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class SessionStore {
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    public void put(String sessionId, Long userId) {
        sessions.put(sessionId, userId);
    }

    public Long get(String sessionId) {
        return sessions.get(sessionId);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    public boolean containsKey(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}
