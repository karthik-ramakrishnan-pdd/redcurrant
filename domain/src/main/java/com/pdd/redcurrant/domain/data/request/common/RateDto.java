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
public class RateDto {

    private String txnExchangeRate;

    private String clientRate;

}
