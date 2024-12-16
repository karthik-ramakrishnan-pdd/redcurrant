package com.digit9.reports.application.provider;

import com.digit9.commons.core.data.dto.idm.UserDetailsDto;
import com.digit9.reports.domain.data.TSPReportsDto;

import java.math.BigInteger;

public final class BasicDataProvider {

    public static final Long REPORT_ID = 1L;

    public static final BigInteger TXN_ID = new BigInteger("23163122406303113822");

    public static final String TSP_ID = "5f1f1cb7-f553-42e6-9695-2f4ba79c1b02";

    public static final TSPReportsDto REPORT = TSPReportsDto.builder()
        .id(REPORT_ID)
        .tspId(TSP_ID)
        .transactionId(BasicDataProvider.TXN_ID)
        .build();

    public static final UserDetailsDto TSP1_USER = UserDetailsDto.builder()
        .id(1L)
        .name("TSP 1")
        .enabled(true)
        .role("SERVICE_PROVIDER")
        .parentId(BasicDataProvider.TSP_ID)
        .build();

}
