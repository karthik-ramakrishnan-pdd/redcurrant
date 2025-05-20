package com.pdd.redcurrant.domain.data.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class ReturnDescriptionDto {

    private BigDecimal exchangeRate;

    private BigDecimal payoutAmt;

    private BigDecimal totalPayinAmt;

    private Map<String, Object> additionalInfo;

    private String wuRoutingCode;

    private BigDecimal commission;

    private BigDecimal tax;

    private BigDecimal payinAmt;

    private String quoteId;

}