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

    @JsonAlias("senderContactNum")
    private String senderContactNo;

    private String senderBeneRelationship;

    @JsonAlias("senderDateOfBirth")
    private String senderDOB;

    private String senderRemarks;

    @JsonAlias("sender_address")
    private AddressDto address;

    @JsonAlias("sender_id")
    private IdDto id;

}
