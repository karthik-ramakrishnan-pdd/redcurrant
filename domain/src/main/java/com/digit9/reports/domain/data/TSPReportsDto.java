package com.digit9.reports.domain.data;

import com.digit9.commons.core.data.dto.BaseDTO;
import com.digit9.commons.core.data.enums.TransactionCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TSPReportsDto extends BaseDTO {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigInteger transactionId;

    private TransactionCategory category;

    private String tspId;

    private Long walletId;

}
