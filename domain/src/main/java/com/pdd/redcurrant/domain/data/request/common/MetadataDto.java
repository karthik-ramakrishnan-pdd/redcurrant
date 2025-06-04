package com.pdd.redcurrant.domain.data.request.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.pdd.redcurrant.domain.annotations.StandardJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@StandardJson
public class MetadataDto {

    private String agentCode;

    private String sendingBranchCode;

    private String companyCode;

    private String method;

    @JsonAlias("exchangeroutingkey")
    private String exchangeRoutingKey;

    private String sourceChannel;

    private String serviceProviderCode;

    private String partnerSmsNotification;

    private String agentEmailId;

    private OrderingInstitutionDto orderingInstitution;

    @Data
    @StandardJson
    public static class OrderingInstitutionDto {

        private String name;

        private String currency;

        private InstitutionAddressDto institutionAddress;

    }

    @Data
    @StandardJson
    public static class InstitutionAddressDto {

        private String countryCode;

        private String townName;

    }

}
