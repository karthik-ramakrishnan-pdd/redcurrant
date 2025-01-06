package com.pdd.redcurrant.domain.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MockDto {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigInteger transactionId;

    private String tspId;

    private Long walletId;

}
