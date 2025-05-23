package com.pdd.redcurrant.domain.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.data.response.VostroBalEnquiryResponseDto;
import com.pdd.redcurrant.domain.exception.GcashException;
import com.redcurrant.downstream.dto.gcash.BalanceErrorResponse;
import com.redcurrant.downstream.dto.gcash.BalanceResponse;
import lombok.experimental.UtilityClass;
import org.springframework.web.client.HttpStatusCodeException;

@UtilityClass
public class GcashMapper {

    public static final String GCASH_PARSE_ERROR_CODE = "GCASH_PARSE_ERROR";

    public static final String GCASH_INVALID_RESPONSE_CODE = "GCASH_INVALID_RESPONSE";

    public VostroBalEnquiryResponseDto of(BalanceResponse balanceResponse) {
        if (balanceResponse.getBalance() == null || balanceResponse.getBalance().getAvailableBalance().isEmpty()) {
            throw new GcashException(GCASH_INVALID_RESPONSE_CODE, "Missing balance information in response");
        }

        return VostroBalEnquiryResponseDto.builder()
            .statusCode(balanceResponse.getCode())
            .balance(balanceResponse.getBalance().getAvailableBalance())
            .build();
    }

    public VostroBalEnquiryResponseDto handleBalEnquiryException(HttpStatusCodeException ex,
            ObjectMapper objectMapper) {
        try {
            BalanceErrorResponse error = objectMapper.readValue(ex.getResponseBodyAsString(),
                    BalanceErrorResponse.class);
            throw new GcashException(error.getCode(), error.getMessage());
        }
        catch (JsonProcessingException parseEx) {
            throw new GcashException(GCASH_PARSE_ERROR_CODE, "Unable to parse GCash error response");
        }
    }

}
