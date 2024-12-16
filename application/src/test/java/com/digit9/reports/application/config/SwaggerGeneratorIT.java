package com.digit9.reports.application.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class SwaggerGeneratorIT extends BaseIntegrationTest {

    public static final String V3_API_URL = "/v3/api-docs";

    public static final String API_DOC_JSON = "/open-api.json";

    private static final String DOCS_FOLDER = "openapi/";

    @Test
    public void generateSwaggerDocumentation() throws Exception {
        File docsDirectory = new File(DOCS_FOLDER);
        if (!docsDirectory.exists()) {
            Files.createDirectory(docsDirectory.toPath());
        }
        if (!docsDirectory.exists()) {
            Assertions.fail("Can't generate the Swagger documentation. Parent /openapi folder missing");
        }

        ResultActions response = mockMvc
            .perform(MockMvcRequestBuilders.get(V3_API_URL).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk());

        String body = response.andReturn().getResponse().getContentAsString();

        String content = objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(objectMapper.readValue(body, Object.class));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(docsDirectory + API_DOC_JSON))) {
            writer.write(content);
        }
        catch (IOException ioException) {
            Assertions.fail("Can't generate the Swagger documentation. IOException when writing file", ioException);
        }
        finally {
            FileUtils.deleteQuietly(new File(docsDirectory + API_DOC_JSON));
        }
    }

}
