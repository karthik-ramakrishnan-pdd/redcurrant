package com.digit9.reports.application.controller;

import com.digit9.reports.application.config.BaseControllerIntegrationTest;
import com.digit9.reports.application.config.security.MockWebSecurityConfigurerAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class TSPReportsControllerIT extends BaseControllerIntegrationTest {

    @Test
    @DisplayName("Get tsp report methods - return 200")
    public void getTspReport() throws Exception {
        obtainAccessToken(MockWebSecurityConfigurerAdapter.TSP_USER, MockWebSecurityConfigurerAdapter.TSP_USER_PASS);
        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/s/api/v1/reports/240231413210461196")
                .headers(withAuthHeaders())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Get tsp report methods - return 401")
    public void unauthenticatedRequestWhenGetTspReport() throws Exception {
        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/s/api/v1/reports/240231413210461196")
                .headers(withoutAuthHeaders())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Get tsp report methods - return 403")
    public void forbiddenRequestWhenGetTspReport() throws Exception {
        obtainAccessToken(MockWebSecurityConfigurerAdapter.MERCHANT_USER,
                MockWebSecurityConfigurerAdapter.MERCHANT_USER_PASS);
        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/s/api/v1/reports/240231413210461196")
                .headers(withAuthHeaders())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}
