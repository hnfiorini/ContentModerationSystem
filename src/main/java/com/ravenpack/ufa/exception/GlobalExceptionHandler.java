package com.ravenpack.ufa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CsvProcessingException.class)
    public ResponseEntity<String> handleCsvException(CsvProcessingException ex) {
        log.error("Error processing CSV", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("An error occurred while processing the CSV file. Please check the format or path.");
    }

    @ExceptionHandler(ResourceLoadException.class)
    public ResponseEntity<String> handleResourceLoad(ResourceLoadException ex) {
        log.error("Resource load error", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal system error.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error occurred while processing the request.");
    }
}
