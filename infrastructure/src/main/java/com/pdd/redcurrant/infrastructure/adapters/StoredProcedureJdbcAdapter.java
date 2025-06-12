package com.pdd.redcurrant.infrastructure.adapters;

import com.pdd.redcurrant.domain.ports.spi.StoredProcedurePort;
import com.pdd.redcurrant.infrastructure.utils.JdbcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoredProcedureJdbcAdapter implements StoredProcedurePort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> executeProcedure(String procedureName, Object... params) {
        // Build the SQL call string
        String callProcedure = JdbcUtils.buildCallStatement(procedureName, params);

        try {
            log.info("Executing stored procedure: {}", procedureName);
            // Executes the procedure and returns the result
            return jdbcTemplate.queryForList(callProcedure, params);
        }
        catch (DataAccessException ex) {
            log.error("Error executing stored procedure: {}", procedureName, ex);
            // Re-throw the exception with a clear message
            throw new RuntimeException("Error executing stored procedure: " + procedureName, ex);
        }
    }

}
