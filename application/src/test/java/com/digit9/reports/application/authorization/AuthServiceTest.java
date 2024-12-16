package com.digit9.reports.application.authorization;

import com.digit9.commons.core.data.dto.idm.UserDetailsDto;
import com.digit9.commons.core.spi.IdmClientPort;
import com.digit9.commons.web.services.AuthServiceImpl;
import com.digit9.reports.application.config.BaseUnitTest;
import com.digit9.reports.application.provider.BasicDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class AuthServiceTest extends BaseUnitTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private IdmClientPort idmClient;

    @Test
    @DisplayName("Get authenticated user - success")
    public void getAuthenticatedUser() {
        Mockito.when(idmClient.getMyInfo()).thenReturn(BasicDataProvider.TSP1_USER);

        UserDetailsDto result = authService.getAuthenticatedUser();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(BasicDataProvider.TSP1_USER, result);
    }

}
