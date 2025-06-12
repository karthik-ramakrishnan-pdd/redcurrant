package com.pdd.redcurrant.domain.data.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AccountDetailsResponseDto extends BaseResponseDto {

    private String acctName;

}