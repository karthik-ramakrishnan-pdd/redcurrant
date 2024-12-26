package com.pdd.redcurrant.domain.ports.api;

import com.pdd.redcurrant.domain.data.RequestDto;
import com.pdd.redcurrant.domain.data.ResponseDto;

public interface GCashServicePort {

    ResponseDto sendTxn(RequestDto request);

    ResponseDto enquiryTxn(RequestDto request);

    ResponseDto vostroBalEnquiry(RequestDto request);

    ResponseDto fetchAcctDtls(RequestDto request);

    ResponseDto bankslist(RequestDto request);

    ResponseDto getPartnerRates(RequestDto request);

    ResponseDto cancelTxn(RequestDto request);

}
