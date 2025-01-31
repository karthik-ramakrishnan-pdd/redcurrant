package com.pdd.redcurrant.domain.ports.api;

import com.pdd.redcurrant.domain.data.ResponseDto;

public interface SolaceServicePort {

    void process(String message);

    ResponseDto processAndReturn(String message);

}
