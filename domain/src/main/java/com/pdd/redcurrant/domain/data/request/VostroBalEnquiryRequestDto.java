package com.pdd.redcurrant.domain.data.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.pdd.redcurrant.domain.annotations.StandardJson;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@StandardJson
public class VostroBalEnquiryRequestDto extends BaseRequestDto {

    // Transaction Identifiers
    private Integer processingType;

    // Account Information
    private String vostroAccountNumber;

    private String companyCode;

    private String serviceProviderCode;

    // Currency Information
    private String senderCurrency;

    private String receiveCurrency;

    @NotNull
    @JsonAlias("exchangeroutingkey")
    private String exchangeRoutingKey;

}