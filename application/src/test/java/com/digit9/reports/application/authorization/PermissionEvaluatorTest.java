package com.digit9.reports.application.authorization;

import com.digit9.reports.application.config.BaseUnitTest;
import com.digit9.reports.application.security.service.PermissionEvaluator;
import com.digit9.reports.domain.ports.api.TSPReportsServicePort;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class PermissionEvaluatorTest extends BaseUnitTest {

    @InjectMocks
    private PermissionEvaluator permissionEvaluator;

    @Mock
    private TSPReportsServicePort tspReportsService;

    // TODO: 5/10/23 Add test cases

}
