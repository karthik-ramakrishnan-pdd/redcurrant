package com.pdd.redcurrant.domain.data.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDetailsDto {

    private String txnRefNum;

    private String agentRefNum;

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
