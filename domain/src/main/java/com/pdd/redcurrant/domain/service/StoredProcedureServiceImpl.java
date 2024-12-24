package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.ports.api.StoredProcedureServicePort;
import com.pdd.redcurrant.domain.ports.spi.StoredProcedurePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class StoredProcedureServiceImpl implements StoredProcedureServicePort {

    private final StoredProcedurePort storedProcedureJdbcAdapter;

    @Override
    public String fetch(String name, Object[] params) {
        return storedProcedureJdbcAdapter.executeProcedure(name, params).toString();
    }
}
