package com.pdd.redcurrant.domain.data.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SenderDto {

    private String customerNo;

    private String senderFirstName;

    private String senderMiddleName;

    private String senderLastName;

    private String senderName;

    private String senderPlaceOfBirth;

    private String senderEmailAddress;

    private String senderProfession;

    private String customerGender;

    private String senderType;

    private String senderNationalityCode;

    private String senderNationality;

    private String senderContactNum;

    private String senderBeneRelationship;

    private String senderDateOfBirth;

    private String senderRemarks;

    @JsonAlias("sender_address")
    private AddressDto address;

    @JsonAlias("sender_id")
    private IdDto id;

}
