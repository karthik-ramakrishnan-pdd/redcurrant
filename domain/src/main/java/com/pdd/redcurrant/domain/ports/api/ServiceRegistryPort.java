package com.pdd.redcurrant.domain.ports.api;

public interface ServiceRegistryPort {

    <T> T invoke(String partnerName, String methodName, String rawJson);

}
