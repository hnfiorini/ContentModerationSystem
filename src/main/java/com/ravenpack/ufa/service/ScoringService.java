package com.ravenpack.ufa.service;

import org.springframework.stereotype.Service;

@Service
public interface ScoringService {

    float score(String translatedMessage);
}
