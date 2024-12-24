package com.pdd.redcurrant.application.controller.internal;

import com.pdd.redcurrant.application.annotations.ExcludeFromJacocoGeneratedReport;
import com.pdd.redcurrant.domain.data.TSPReportsDto;
import com.pdd.redcurrant.domain.ports.api.StoredProcedureServicePort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "v1/internal")
@Tag(name = "Internal Endpoints")
@ExcludeFromJacocoGeneratedReport
@Slf4j
public class InternalEndpointController {

    private final StoredProcedureServicePort storedProcedureService;

    private final JmsTemplate jmsTemplate;

    @Value("${spring.jms.default-destination}")
    private String topic;

    @GetMapping(path = "procedure/{name}")
    public String fetch(@PathVariable(name = "name") String name, @RequestParam(name = "params", required = false) List<String> params) {
        return storedProcedureService.fetch(name, params.toArray());
    }

    @PostMapping(path = "solace")
    public void test(@RequestBody TSPReportsDto request) {
        publish(MapperUtils.toString(request));
    }

    private void publish(String message) {
        jmsTemplate.convertAndSend(topic, message);
        log.info("Published: {}", message);
    }

}