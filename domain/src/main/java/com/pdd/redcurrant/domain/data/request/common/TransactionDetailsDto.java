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
public class TransactionDetailsDto {

    @JsonAlias("txnRefNo")
    private String txnRefNo;

    @JsonAlias("agentRefNum")
    private String agentRefNo;

    private String txnValueDate;

    private String txnDate;

    private String sourceOfIncomeDesc;

    private String sourceOfIncomeCode;

    private String purposeCode;

    private String purposeDesc;

    private String instrumentType;

    private String txnMode;

    @JsonAlias("pymtMode")
    private String paymentMode;

    @JsonAlias("from_sender")
    private SenderReceiverDto fromSender;

    @JsonAlias("to_receiver")
    private SenderReceiverDto toReceiver;

    private SettlementDto settlement;

    private RateDto rate;

    @JsonAlias("opted_service")
    private ServiceInfoDto optedService;

}
