package com.pdd.redcurrant.domain.data.request;

import com.pdd.redcurrant.domain.annotations.StandardJson;
import com.pdd.redcurrant.domain.data.request.common.MetadataDto;
import com.pdd.redcurrant.domain.data.request.common.ReceiverDto;
import com.pdd.redcurrant.domain.data.request.common.SenderDto;
import com.pdd.redcurrant.domain.data.request.common.TransactionDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@StandardJson
public class RequestDto extends BaseRequestDto {

    private MetadataDto metadata;

    private SenderDto sender;

    private ReceiverDto receiver;

    private TransactionDetailsDto transaction;

}
