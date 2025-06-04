package com.pdd.redcurrant.domain.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gcash")
public class GCashPropertiesConfig {

    private String privateKey;

    private String publicKey;

    private String keyAlgorithm;

    private String clientId;

    private String clientSecret;

    private String headFunction;

    private String headVersion;

    private String walletName;

    private String walletPin;

}
