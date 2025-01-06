package com.pdd.redcurrant.domain.data.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdDto {

    private String senderIDIssueDate;

    private String senderId;

    private String senderIdType;

    private String senderIdNum;

    private String senderIdIssueCountry;

    private String senderIDExpiryOn;

    private String beneficiaryIdNum;

    private String beneficiaryIDType;

}
