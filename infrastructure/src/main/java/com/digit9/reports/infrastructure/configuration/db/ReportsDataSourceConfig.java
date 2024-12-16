package com.digit9.reports.infrastructure.configuration.db;

import com.digit9.commons.infra.configuration.db.AbstractDataSourceConfig;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:digit9/postgres.properties")
@EntityScan(ReportsDataSourceConfig.ENTITY_PACKAGE)
@EnableJpaRepositories(basePackages = ReportsDataSourceConfig.REPO_PACKAGE,
        entityManagerFactoryRef = ReportsDataSourceConfig.BASE_PROPERTY_NAME + "_Em",
        transactionManagerRef = ReportsDataSourceConfig.BASE_PROPERTY_NAME + "_TxnManager")
public class ReportsDataSourceConfig extends AbstractDataSourceConfig {

    protected static final String ENTITY_PACKAGE = "com.digit9.reports.infrastructure.entity";

    protected static final String REPO_PACKAGE = "com.digit9.reports.infrastructure.repository";

    protected static final String BASE_PROPERTY = "spring.datasource.reports";

    protected static final String BASE_PROPERTY_NAME = "DS_REPORTS";

    @Bean
    @Primary
    @ConfigurationProperties(prefix = ReportsDataSourceConfig.BASE_PROPERTY)
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(BASE_PROPERTY_NAME + "_Em")
    @Primary
    public LocalContainerEntityManagerFactoryBean reportsEntityManagerFactory() {
        return createEntityManagerFactory();
    }

    @Bean(BASE_PROPERTY_NAME + "_TxnManager")
    @Primary
    public JpaTransactionManager reportsTransactionManager(EntityManagerFactory primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory);
    }

    @Override
    protected DataSource getDatasource() {
        return primaryDataSource();
    }

    @Override
    protected String[] getEntityPackages() {
        return new String[] { ENTITY_PACKAGE };
    }

}