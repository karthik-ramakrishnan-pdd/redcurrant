package com.pdd.redcurrant.domain.data.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SenderReceiverDto {

    private String sendCountry;

    private String senderCurrency;

    private String amountFromSender;

    private String serviceFee;

    private String serviceFeeCcy;

    private String merchantFee;

    private String amountToSend;

    private String payoutCountryCode;

    private String receiveCurrency;

}
