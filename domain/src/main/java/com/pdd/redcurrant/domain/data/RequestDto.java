package com.pdd.redcurrant.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pdd.redcurrant.domain.data.common.MetadataDto;
import com.pdd.redcurrant.domain.data.common.ReceiverDto;
import com.pdd.redcurrant.domain.data.common.SenderDto;
import com.pdd.redcurrant.domain.data.common.TransactionDetailsDto;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestDto {

    private MetadataDto metadata;

    private SenderDto sender;

    private ReceiverDto receiver;

    private TransactionDetailsDto transaction;

}
