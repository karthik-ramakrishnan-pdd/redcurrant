package com.pdd.redcurrant;

import com.pdd.redcurrant.application.annotations.RCSpringBootApplication;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.ZoneOffset;
import java.util.TimeZone;

@EnableJpaAuditing
@RCSpringBootApplication
public class RCPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RCPlatformApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

}
