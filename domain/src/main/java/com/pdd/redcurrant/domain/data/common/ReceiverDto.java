package com.pdd.redcurrant.domain.data.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiverDto {

    private String beneficiaryType;

    private String beneficiaryID;

    private String beneficiaryName;

    private String beneficiaryFirstName;

    private String beneficiaryMiddleName;

    private String beneficiaryLastName;

    private String beneficiaryNationality;

    private String beneficiaryContactNum;

    private String beneficiaryPhone;

    private String beneficiaryRemarks;

    @JsonAlias("receiver_address")
    private AddressDto address;

    @JsonAlias("receiver_id")
    private IdDto id;

}
