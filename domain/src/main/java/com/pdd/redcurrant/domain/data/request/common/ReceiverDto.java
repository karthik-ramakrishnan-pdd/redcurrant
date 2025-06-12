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
public class ReceiverDto {

    private String beneficiaryType;

    private String beneficiaryID;

    private String beneficiaryName;

    private String beneficiaryFirstName;

    private String beneficiaryMiddleName;

    private String beneficiaryLastName;

    private String beneficiaryNationality;

    @JsonAlias("beneficiaryContactNum")
    private String beneficiaryContactNo;

    private String beneficiaryPhone;

    private String beneficiaryRemarks;

    @JsonAlias("receiver_address")
    private AddressDto address;

    @JsonAlias("receiver_id")
    private IdDto id;

}
