package com.ordermedicine.service.order_medicine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private StreamBridge streamBridge;

    public OrderController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @GetMapping("/order")
    public void order() {
        boolean send = streamBridge.send("orderCreatedEvent-out-0", "order");
        if(send) {
            log.info("Order notification sent successfully");
        }
    }
}
