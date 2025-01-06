package com.pdd.redcurrant.domain.data.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataDto {

    private String agentCode;

    private String sendingBranchCode;

    private String companyCode;

    @NotNull
    private String method;

    @NotNull
    @JsonAlias("exchangeroutingkey")
    private String exchangeRoutingKey;

    private String sourceChannel;

    private String serviceProviderCode;

    private String partnerSmsNotification;

    private String agentEmailId;

    private OrderingInstitutionDto orderingInstitution;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class OrderingInstitutionDto {

        private String name;

        private String currency;

        private InstitutionAddressDto institutionAddress;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class InstitutionAddressDto {

        private String countryCode;

        private String townName;

    }

}
