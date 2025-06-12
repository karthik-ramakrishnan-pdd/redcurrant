package com.pdd.redcurrant.domain.data.request.common;

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
