package com.pdd.redcurrant.domain.data.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class VostroBalEnquiryResponseDto extends BaseResponseDto {

    // Transaction Identifiers
    private String companyCode;

    private String serviceProviderCode;

    private String routingKey;

    // Balance Information
    private String balance;

}