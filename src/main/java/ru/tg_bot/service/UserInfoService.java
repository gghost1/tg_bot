package ru.tg_bot.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In future may be replaced by redis
 */

@Service
@Scope("singleton")
public class UserInfoService {
    private final ConcurrentHashMap<String, String> storage = new ConcurrentHashMap<>();

    public Optional<String> getState(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void setState(String id, String state) {
        storage.put(id, state);
    }

    public void removeState(String id) {
        storage.remove(id);
    }

    public Optional<String> getSubInfo(String id, String subInfoTag) {
        return Optional.ofNullable(storage.get(id + subInfoTag));
    }

    public void setSubInfo(String id, String subInfoTag, String subInfo) {
        storage.put(id + subInfoTag, subInfo);
    }

    public void removeSubInfo(String id, String subInfoTag) {
        storage.remove(id + subInfoTag);
    }

}
