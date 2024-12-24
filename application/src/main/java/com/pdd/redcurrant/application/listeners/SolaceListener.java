package com.pdd.redcurrant.application.listeners;

import com.pdd.redcurrant.domain.data.TSPReportsDto;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolaceListener {

    @JmsListener(destination = "${spring.jms.default-destination}")
    public void receiveMessage(String message) {
        log.info("Received: {}", message);
        var test = MapperUtils.convert(message, TSPReportsDto.class);
        log.info("Id - {}, Wallet Id - {}, TSP Id - {}, Transaction Id - {}", test.getId(), test.getWalletId(),
                test.getTspId(), test.getTransactionId());
    }

}
