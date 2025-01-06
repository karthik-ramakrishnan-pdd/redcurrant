package com.pdd.redcurrant.domain.ports.spi;

import java.util.List;
import java.util.Map;

public interface StoredProcedurePort {

    List<Map<String, Object>> executeProcedure(String procedureName, Object... params);

}
