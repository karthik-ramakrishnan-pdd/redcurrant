package com.digit9.reports.infrastructure;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.digit9.reports.infrastructure", "com.digit9.commons.infra" })
public class TestApplication {

}
