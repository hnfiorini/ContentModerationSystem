package com.ravenpack.ufa.unit;

import com.ravenpack.ufa.model.InputMessage;
import com.ravenpack.ufa.model.UserAggregationData;
import com.ravenpack.ufa.service.CsvProcessor;
import com.ravenpack.ufa.service.ScoringService;
import com.ravenpack.ufa.service.TranslationService;
import com.ravenpack.ufa.service.UserFlagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserFlagServiceTest {

    @Mock
    CsvProcessor csvProcessor;
    @Mock
    TranslationService translationService;
    @Mock
    ScoringService scoringService;

    @InjectMocks
    UserFlagService userFlagService;

    @Captor
    private ArgumentCaptor<Map<String, UserAggregationData>> captor;

    @Test
    void shouldProcessMessagesAndGenerateOutput() throws IOException {
        List<InputMessage> messages = List.of(
                new InputMessage("user1", "Hola"),
                new InputMessage("user1", "Hello"),
                new InputMessage("user2", "Bonjour")
        );

        Mockito.doAnswer(invocation -> {
            Consumer<InputMessage> consumer = invocation.getArgument(1);
            messages.forEach(consumer);
            return null;
        }).when(csvProcessor).readMessages(anyString(), any());

        when(translationService.translate("Hola")).thenReturn("Hello");
        when(translationService.translate("Bonjour")).thenReturn("Hello");
        when(translationService.translate("Hello")).thenReturn("Hello");
        when(scoringService.score("Hello")).thenReturn(0.8f);

        userFlagService.run("input.csv", "output.csv");

        verify(csvProcessor).writeOutputData(eq("output.csv"), captor.capture());

        Map<String, UserAggregationData> aggregatedData = captor.getValue();

        assertThat(aggregatedData).hasSize(2);
        assertThat(aggregatedData).containsOnlyKeys("user1", "user2");

        UserAggregationData user1Data = aggregatedData.get("user1");
        assertThat(user1Data.getTotalMessages()).isEqualTo(2);
        assertThat(user1Data.getAverage()).isEqualTo(0.8d, within(0.001d));

        UserAggregationData user2Data = aggregatedData.get("user2");
        assertThat(user2Data.getTotalMessages()).isEqualTo(1);
        assertThat(user2Data.getAverage()).isEqualTo(0.8d, within(0.001d));

    }
}
