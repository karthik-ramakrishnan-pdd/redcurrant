package com.digit9.reports.infrastructure.config;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Sets up the base configurations for tests.
 */
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public abstract class TestConfiguration {

}
