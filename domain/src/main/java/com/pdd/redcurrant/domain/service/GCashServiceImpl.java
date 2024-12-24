package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.mappers.GCashMapper;
import com.pdd.redcurrant.domain.ports.api.GCashServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GCashServiceImpl implements GCashServicePort {

    private final GCashMapper gCashMapper;

}