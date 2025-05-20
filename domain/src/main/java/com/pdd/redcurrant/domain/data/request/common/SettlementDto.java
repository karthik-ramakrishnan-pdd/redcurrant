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
public class SettlementDto {

    private String xchgRatePayinToPayout;

    private String xchgRateSettlementToPayout;

    private String xchgRateUSDToPayout;

    private String xchgRateUSDToPayin;

}
