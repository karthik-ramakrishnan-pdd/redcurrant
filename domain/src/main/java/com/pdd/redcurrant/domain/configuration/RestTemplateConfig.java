package com.pdd.redcurrant.domain.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@Slf4j
public class RestTemplateConfig {

    private static final ThreadLocal<String> rawJsonHolder = new ThreadLocal<>();

    public static String getLastRawJson() {
        return rawJsonHolder.get();
    }

    @Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        RestTemplate template = new RestTemplate();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        template.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
        template.getMessageConverters().addFirst(converter);

        template.setInterceptors(List.of((request, body, execution) -> {
            ClientHttpResponse original = execution.execute(request, body);
            byte[] buffered = original.getBody().readAllBytes();
            rawJsonHolder.set(new String(buffered, StandardCharsets.UTF_8));
            return new ClientHttpResponseWrapper(original, buffered);
        }));

        return template;
    }

    private record ClientHttpResponseWrapper(ClientHttpResponse delegate, byte[] body) implements ClientHttpResponse {

        @Override
        public @NonNull InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public @NonNull HttpHeaders getHeaders() {
            return delegate.getHeaders();
        }

        @Override
        public @NonNull HttpStatusCode getStatusCode() throws IOException {
            return delegate.getStatusCode();
        }

        @Override
        public @NonNull String getStatusText() throws IOException {
            return delegate.getStatusText();
        }

        @Override
        public void close() {
            delegate.close();
        }
    }

}
