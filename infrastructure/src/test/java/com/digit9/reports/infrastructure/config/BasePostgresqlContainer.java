package com.digit9.reports.infrastructure.config;

import org.testcontainers.containers.PostgreSQLContainer;

public final class BasePostgresqlContainer extends PostgreSQLContainer<BasePostgresqlContainer> {

    private static final String IMAGE_VERSION = "postgres:11.6";

    private static BasePostgresqlContainer container;

    private BasePostgresqlContainer() {
        super(IMAGE_VERSION);
    }

    public static BasePostgresqlContainer getInstance() {
        if (container == null) {
            container = new BasePostgresqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        // do nothing, JVM handles shut down
    }

}
