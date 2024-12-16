package com.digit9.reports.infrastructure.config;

import com.digit9.commons.infra.feignclient.clients.idm.IdmClient;
import com.digit9.reports.infrastructure.repository.TSPReportsRepository;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.containers.PostgreSQLContainer;

@EnableJpaAuditing
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseJpaIntegrationTest extends TestConfiguration {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = BasePostgresqlContainer.getInstance();

    @MockBean
    protected IdmClient idmClient;

    @Autowired
    protected TSPReportsRepository tspReportsRepository;

    @BeforeAll
    static void configureAuditing() {
        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(authentication.getName()).thenReturn("1");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    protected final void cleanUpTestData() {
        tspReportsRepository.deleteAll();
    }

}
