package com.pdd.redcurrant.domain.data.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettlementDto {

    private String xchgRatePayinToPayout;

    private String xchgRateSettlementToPayout;

    private String xchgRateUSDToPayout;

    private String xchgRateUSDToPayin;

}
