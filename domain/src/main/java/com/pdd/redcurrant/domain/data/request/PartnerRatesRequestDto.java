package com.pdd.redcurrant.domain.data.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.pdd.redcurrant.domain.annotations.StandardJson;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@StandardJson
public class PartnerRatesRequestDto extends BaseRequestDto {

    // Transaction Identifiers
    private String reqId;

    private String txnRefNum;

    private String txnType;

    private Integer processingType;

    private String productCode;

    @NotNull
    @JsonAlias("exchangeroutingkey")
    private String exchangeRoutingKey;

    // Amounts and Pricing
    private BigDecimal amountToSend;

    private BigDecimal amountFromSender;

    private BigDecimal sellmarkup;

    // Currency Information
    private String payoutCurrency;

    private String receiveCurrency;

    private String payinCurrency;

    private String senderCurrency;

    // Country Information
    private String sendCountry;

    private String payoutCountry;

    private String senderCountry;

    private String payinCountry;

    // Partner/Agent Information
    private String agentLocationID;

    private String companyCode;

    private String payinCompany;

    private String serviceProviderCode;

    // System Information
    private String sendingHostName;

}