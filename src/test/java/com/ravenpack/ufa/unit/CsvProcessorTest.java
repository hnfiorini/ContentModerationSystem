package com.ravenpack.ufa.unit;

import com.ravenpack.ufa.model.InputMessage;
import com.ravenpack.ufa.model.UserAggregationData;
import com.ravenpack.ufa.service.CsvProcessor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CsvProcessorTest {

    private final CsvProcessor csvProcessor = new CsvProcessor();

    @Test
    void shouldReadMessagesCorrectly() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        Files.write(tempFile.toPath(), List.of("user_id,message", "u1,hello", "u2,world"));

        List<InputMessage> result = new ArrayList<>();

        csvProcessor.readMessages(tempFile.getAbsolutePath(), result::add);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo("u1");
        assertThat(result.get(1).getMessage()).isEqualTo("world");
    }

    @Test
    void shouldWriteOutputCorrectly() throws IOException {

        File tempOutput = File.createTempFile("test-output", ".csv");

        UserAggregationData userAggregationData1 = new UserAggregationData();
        userAggregationData1.addMessage(0.75F);
        userAggregationData1.addMessage(0.25F);

        UserAggregationData userAggregationData2 = new UserAggregationData();
        userAggregationData2.addMessage(0.42d);

        Map<String, UserAggregationData> dataMap = new ConcurrentHashMap<>();
        dataMap.put("user1", userAggregationData1);
        dataMap.put("user2", userAggregationData2);

        csvProcessor.writeOutputData(tempOutput.getAbsolutePath(), dataMap);

        List<String> lines = Files.readAllLines(tempOutput.toPath());
        assertThat(lines).hasSize(3); // header + 2 records
        assertThat(lines.get(0)).contains("user_id,total_messages,avg_score");
        assertThat(lines.get(1)).contains("user1,2,0.5");
        assertThat(lines.get(2)).contains("user2,1,0.42");
    }
}
