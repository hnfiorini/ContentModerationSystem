package com.ravenpack.ufa.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ravenpack.ufa.service.ScoringService;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ScoringServiceImpl implements ScoringService {

    private final Cache<String, Float> cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    private final Random random = new Random();

    @Override
    public float score(String translatedMessage) {
        return cache.get(translatedMessage, this::callScoringApi);
    }

    private float callScoringApi(String message) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {}
        return random.nextFloat();
    }
}
