package com.ravenpack.ufa.service;

import org.springframework.stereotype.Service;

@Service
public interface TranslationService {

    String translate(String message);
}
