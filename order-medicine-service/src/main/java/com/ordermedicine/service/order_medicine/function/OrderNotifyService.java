package com.ordermedicine.service.order_medicine.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class OrderNotifyService {

    private static final Logger log = LoggerFactory.getLogger(OrderNotifyService.class);

    @Bean
    public Consumer<String> orderEventReceiver() {
        return (message) -> {
           log.info(" Received ack for order: {}", message);
        };
    }
}
