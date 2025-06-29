package com.ravenpack.ufa.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ravenpack.ufa.service.TranslationService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TranslationServiceImpl implements TranslationService {

    private final Cache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    @Override
    public String translate(String message) {
        return cache.get(message, this::callTranslationApi);
    }

    private String callTranslationApi(String message) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {}
        return message;
    }
}
