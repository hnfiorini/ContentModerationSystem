package com.ravenpack.ufa.controller;

import com.ravenpack.ufa.exception.ResourceLoadException;
import com.ravenpack.ufa.service.UserFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class InitController {

    private final UserFlagService userFlagService;

    @GetMapping
    public ResponseEntity<String> processCsv(
            @RequestParam(name = "inputFileName", required = false) String inputFileName,
            @RequestParam(name = "outputFileName", required = false, defaultValue = "output.csv") String outputFileName) {

        try {
            if (inputFileName == null || inputFileName.isBlank()) {
                File defaultFile = new ClassPathResource("input.csv").getFile();
                inputFileName = defaultFile.getAbsolutePath();
            }

            log.info("Starting content moderation for file: {} → {}", inputFileName, outputFileName);
            userFlagService.run(inputFileName, outputFileName);
            return ResponseEntity.ok("Content moderation completed successfully.");

        } catch (IOException e) {
            log.error("Error processing CSV", e);
            throw new ResourceLoadException("Could not load default input.csv from classpath", e);
        }
    }

}
