package com.digit9.reports.application.config;

import com.digit9.commons.web.security.handler.AccessDeniedExceptionHandler;
import com.digit9.commons.web.security.handler.AuthenticationExceptionHandler;
import com.digit9.reports.application.config.security.MockAuthorizationServerConfiguration;
import com.digit9.reports.application.config.security.MockWebSecurityConfigurerAdapter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Sets up the configurations for Controller integration tests.
 */
@WebMvcTest
@Import({ AuthenticationExceptionHandler.class, AccessDeniedExceptionHandler.class })
public class BaseControllerIntegrationTest extends BaseIntegrationTest {

    private String token;

    protected void obtainAccessToken(final String username, final String password) throws Exception {
        final MvcResult tokenResult = this.mockMvc
            .perform(MockMvcRequestBuilders.post("/oauth/token?grant_type=password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param(MockAuthorizationServerConfiguration.CLIENT_ID_KEY,
                        MockAuthorizationServerConfiguration.CLIENT_ID)
                .param(MockAuthorizationServerConfiguration.CLIENT_SECRET_KEY,
                        MockAuthorizationServerConfiguration.CLIENT_SECRET)
                .param(MockWebSecurityConfigurerAdapter.USERNAME_KEY, username)
                .param(MockWebSecurityConfigurerAdapter.PASSWORD_KEY, password))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        Assertions.assertNotNull(tokenResult);
        final ObjectNode tokenResponse = this.objectMapper.readValue(tokenResult.getResponse().getContentAsString(),
                ObjectNode.class);
        Assertions.assertNotNull(tokenResponse);
        Assertions.assertTrue(tokenResponse.has("access_token"));
        this.token = tokenResponse.get("access_token").textValue();
    }

    protected String getBearerToken() {
        return String.format("Bearer %s", this.token);
    }

    protected HttpHeaders withAuthHeaders() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, getBearerToken());
        return httpHeaders;
    }

    protected HttpHeaders withoutAuthHeaders() {
        return new HttpHeaders();
    }

}
