package com.notification.service.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Bean
    public Function<String, String> orderEventReceiver() {
        return (message) -> {
            log.info("Sending notification for order: {}", message);
            return "Notification sent";
        };
    }
}
