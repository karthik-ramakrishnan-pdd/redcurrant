package com.pdd.redcurrant.domain.ports.api;

public interface StoredProcedureServicePort {

    String fetch(String name, Object[] params);

}
