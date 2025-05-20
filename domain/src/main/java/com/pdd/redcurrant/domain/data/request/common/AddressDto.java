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
public class AddressDto {

    @JsonAlias({ "senderAddress", "beneficiaryAddress" })
    private String address;

    @JsonAlias("beneficiaryAddress1")
    private String address1;

    @JsonAlias("beneficiaryAddress2")
    private String address2;

    @JsonAlias({ "senderCity", "beneficiaryCity" })
    private String city;

    @JsonAlias({ "senderState", "beneficiaryState" })
    private String state;

    @JsonAlias({ "senderCountry", "beneficiaryCountry" })
    private String country;

    @JsonAlias({ "senderPincode", "beneficiaryPincode" })
    private String pinCode;

    @JsonAlias("sendCountryName")
    private String countryName;

}
