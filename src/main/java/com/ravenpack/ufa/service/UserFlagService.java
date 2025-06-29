package com.ravenpack.ufa.service;

import com.ravenpack.ufa.exception.CsvProcessingException;
import com.ravenpack.ufa.model.UserAggregationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFlagService {

    private final CsvProcessor csvProcessor;
    private final TranslationService translationService;
    private final ScoringService scoringService;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void run(String inputFilePath, String outputFilePath) {

        try {
            Map<String, UserAggregationData> userScoresMap = new ConcurrentHashMap<>();

            csvProcessor.readMessages(inputFilePath, inputMessage -> {
                CompletableFuture.runAsync(() -> {
                    try {
                        String translated = translationService.translate(inputMessage.getMessage());
                        float score = scoringService.score(translated);
                        userScoresMap.compute(inputMessage.getUserId(), (userId, data) -> {
                            if (data == null) data = new UserAggregationData();
                            data.addMessage(score);
                            return data;
                        });
                    } catch (Exception e) {
                        System.err.println("Error processing message: " + e.getMessage());
                    }
                }, executor);
            });

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);

            csvProcessor.writeOutputData(outputFilePath, userScoresMap);
        } catch (IOException e) {
            throw new CsvProcessingException("Error processing CSV files", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("The process was interrupted.", e);
        }

    }

}
