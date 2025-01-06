package com.pdd.redcurrant.domain.data.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
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
