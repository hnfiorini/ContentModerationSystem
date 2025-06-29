package com.ravenpack.ufa;

import com.ravenpack.ufa.config.MockServicesConfig;
import com.ravenpack.ufa.service.ScoringService;
import com.ravenpack.ufa.service.TranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@Import(MockServicesConfig.class)
@SpringBootTest(classes = UserFlagApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TranslationService translationService;
    @Autowired
    ScoringService scoringService;

    private Path inputFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() throws IOException {
        inputFile = tempDir.resolve("input.csv");
        Files.copy(getClass().getClassLoader().getResourceAsStream("input.csv"), inputFile);
    }

    @Test
    void shouldProcessFileAndReturn200() throws Exception {
        when(translationService.translate(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(scoringService.score(anyString())).thenReturn(0.5F);

        String outputFilename = tempDir.resolve("output.csv").toString();
        File outputFile = new File(outputFilename);

        mockMvc.perform(get("/process")
                        .param("inputFileName", inputFile.toString())
                        .param("outputFileName", outputFilename))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Content moderation completed successfully.")));

        await().atMost(5, TimeUnit.SECONDS).until(outputFile::exists);

        assertThat(outputFile).exists();

        List<String> allLines = Files.readAllLines(outputFile.toPath());
        assertThat(allLines).hasSize(3);
        assertThat(allLines.get(0)).contains("user_id,total_messages,avg_score");

        Optional<String> user_1 = allLines.stream()
                .filter(linea -> linea.startsWith("user_1"))
                .findFirst();
        assertThat(user_1).isPresent();
        assertThat(user_1.get()).contains("user_1,2,0.5");

        Optional<String> user_2 = allLines.stream()
                .filter(linea -> linea.startsWith("user_2"))
                .findFirst();
        assertThat(user_2).isPresent();
        assertThat(user_2.get()).contains("user_2,1,0.5");

    }
}
