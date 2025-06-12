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
public class IdDto {

    @JsonAlias("senderIDIssueDate")
    private String senderIdIssueDate;

    private String senderId;

    private String senderIdType;

    @JsonAlias("senderIdNum")
    private String senderIdNo;

    private String senderIdIssueCountry;

    @JsonAlias("senderIDExpiryOn")
    private String senderIdExpiryOn;

    @JsonAlias("beneficiaryIdNum")
    private String beneficiaryIdNo;

    @JsonAlias("beneficiaryIDType")
    private String beneficiaryIdType;

}
