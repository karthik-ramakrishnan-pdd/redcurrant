package com.pdd.redcurrant.domain.data.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.pdd.redcurrant.domain.annotations.StandardJson;
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
public class BankRequestDto extends BaseRequestDto {

    private String serviceProviderCode;

    @JsonAlias("agentRefNum")
    private String agentRefNo;

    private String agentCode;

    private String payoutCurrency;

    private String payoutCountry;

    private String payinCurrency;

    private String payinCountry;

    private String companyCode;

    private String txnType;

}
