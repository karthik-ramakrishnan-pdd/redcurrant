package com.pdd.redcurrant.domain.ports.api;

import com.pdd.redcurrant.domain.annotations.Partner;
import com.pdd.redcurrant.domain.constants.PartnerConstants;
import com.pdd.redcurrant.domain.data.request.BankRequestDto;
import com.pdd.redcurrant.domain.data.request.CancelTxnRequestDto;
import com.pdd.redcurrant.domain.data.request.PartnerRatesRequestDto;
import com.pdd.redcurrant.domain.data.request.RequestDto;
import com.pdd.redcurrant.domain.data.request.VostroBalEnquiryRequestDto;
import com.pdd.redcurrant.domain.data.response.AccountDetailsResponseDto;
import com.pdd.redcurrant.domain.data.response.BankResponseDto;
import com.pdd.redcurrant.domain.data.response.CancelTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.EnquiryResponseDto;
import com.pdd.redcurrant.domain.data.response.PartnerRatesResponseDto;
import com.pdd.redcurrant.domain.data.response.PreSendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.SendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.VostroBalEnquiryResponseDto;

@Partner(PartnerConstants.PARTNER_GCASH)
public interface GcashServicePort {

    SendTxnResponseDto sendTxn(RequestDto request);

    PreSendTxnResponseDto preSendTxn(RequestDto request);

    EnquiryResponseDto enquiryTxn(RequestDto request);

    VostroBalEnquiryResponseDto vostroBalEnquiry(VostroBalEnquiryRequestDto request);

    AccountDetailsResponseDto fetchAcctDtls(RequestDto request);

    BankResponseDto bankslist(BankRequestDto request);

    PartnerRatesResponseDto getPartnerRates(PartnerRatesRequestDto request);

    CancelTxnResponseDto cancelTxn(CancelTxnRequestDto request);

}
