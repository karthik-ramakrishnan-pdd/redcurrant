package com.digit9.reports.infrastructure.configuration.db;

import com.digit9.commons.infra.configuration.db.AbstractDataSourceConfig;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EntityScan(PaymentDataSourceConfig.ENTITY_PACKAGE)
@EnableJpaRepositories(basePackages = PaymentDataSourceConfig.REPO_PACKAGE,
        entityManagerFactoryRef = PaymentDataSourceConfig.BASE_PROPERTY_NAME + "_Em",
        transactionManagerRef = PaymentDataSourceConfig.BASE_PROPERTY_NAME + "_TxnManager")
public class PaymentDataSourceConfig extends AbstractDataSourceConfig {

    protected static final String ENTITY_PACKAGE = "com.digit9.reports.infrastructure.entity.payment";

    protected static final String REPO_PACKAGE = "com.digit9.reports.infrastructure.repository.payment";

    protected static final String BASE_PROPERTY = "spring.datasource.payment";

    protected static final String BASE_PROPERTY_NAME = "DS_PAYMENT";

    @Bean
    @ConfigurationProperties(prefix = PaymentDataSourceConfig.BASE_PROPERTY)
    public DataSource paymentDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(BASE_PROPERTY_NAME + "_Em")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return createEntityManagerFactory();
    }

    @Bean(BASE_PROPERTY_NAME + "_TxnManager")
    public JpaTransactionManager primaryTransactionManager(EntityManagerFactory primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory);
    }

    @Override
    protected DataSource getDatasource() {
        return paymentDataSource();
    }

    @Override
    protected String[] getEntityPackages() {
        return new String[] { ENTITY_PACKAGE };
    }

}