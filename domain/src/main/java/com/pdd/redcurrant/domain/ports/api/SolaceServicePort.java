package com.pdd.redcurrant.domain.ports.api;

import com.pdd.redcurrant.domain.data.MockDto;

public interface SolaceServicePort {

    void process(String message);

    MockDto processAndReturn(String message);

}
