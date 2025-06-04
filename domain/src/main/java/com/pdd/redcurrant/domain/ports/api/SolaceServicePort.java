package com.pdd.redcurrant.domain.ports.api;

public interface SolaceServicePort {

    Object process(String message);

}
