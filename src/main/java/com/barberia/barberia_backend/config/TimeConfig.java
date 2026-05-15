package com.barberia.barberia_backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class TimeConfig {

    public static final ZoneId ZONE_ID = ZoneId.of("America/Guayaquil");

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZONE_ID));
    }

    @Bean
    public Clock clock() {
        return Clock.system(ZONE_ID);
    }
}
