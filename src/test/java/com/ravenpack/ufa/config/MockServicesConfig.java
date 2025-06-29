package com.ravenpack.ufa.config;

import com.ravenpack.ufa.service.ScoringService;
import com.ravenpack.ufa.service.TranslationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockServicesConfig {

    @Bean
    public TranslationService translationService() {
        return mock(TranslationService.class);
    }

    @Bean
    public ScoringService scoringService() {
        return mock(ScoringService.class);
    }
}
