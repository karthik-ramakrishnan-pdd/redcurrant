package com.pdd.redcurrant.domain.configuration;

import com.redcurrant.downstream.api.gcash.GcashBalanceApi;
import com.redcurrant.downstream.api.gcash.GcashRemitApi;
import com.redcurrant.downstream.client.gcash.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GcashApiClientConfig {

    @Bean
    @Qualifier("gcashRemitClient")
    public ApiClient gcashRemitClient(@Value("${gcash.remit.base-url}") String baseUrl, RestTemplate restTemplate) {
        return new ApiClient(restTemplate).setBasePath(baseUrl);
    }

    @Bean
    @Qualifier("gcashBalanceClient")
    public ApiClient gcashBalanceClient(@Value("${gcash.balance.base-url}") String baseUrl) {
        return new ApiClient().setBasePath(baseUrl);
    }

    @Bean
    public GcashBalanceApi balanceApi(@Qualifier("gcashBalanceClient") ApiClient client) {
        return new GcashBalanceApi(client);
    }

    @Bean
    public GcashRemitApi remittanceApi(@Qualifier("gcashRemitClient") ApiClient client) {
        return new GcashRemitApi(client);
    }

}
