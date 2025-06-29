package com.ravenpack.ufa.service;

import com.ravenpack.ufa.model.InputMessage;
import com.ravenpack.ufa.model.UserAggregationData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Service
public class CsvProcessor {

    public void readMessages(String filePath, Consumer<InputMessage> consumer) throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader("user_id", "message")
                    .setSkipHeaderRecord(true)
                    .setDelimiter(',')
                    .build().parse(reader);

            for (CSVRecord record : records) {
                String userId = record.get("user_id");
                String message = record.get("message");
                consumer.accept(new InputMessage(userId, message));
            }
        }
    }

    public void writeOutputData(String filePath, Map<String, UserAggregationData> userAggregationDataMap) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.builder()
                             .setHeader("user_id", "total_messages", "avg_score")
                             .setDelimiter(',')
                             .build())) {

            for (Map.Entry<String, UserAggregationData> entry : userAggregationDataMap.entrySet()) {
                UserAggregationData aggregationData = entry.getValue();
                printer.printRecord(entry.getKey(), aggregationData.getTotalMessages(), aggregationData.getAverage());
            }
        }
    }
}
