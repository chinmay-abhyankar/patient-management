package com.pm.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(GlobalLoggingFilter.class);

    private final boolean loggingEnabled;

    public GlobalLoggingFilter(@Value("${logging.filter.enabled:true}") boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!loggingEnabled) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        long startTime = System.currentTimeMillis();

        logger.info("Incoming request: method={}, uri={}, headers={}",
                request.getMethod(),
                request.getURI(),
                request.getHeaders());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Outgoing response: status={}, duration={}ms",
                    response.getStatusCode(),
                    duration);
        }));
    }

    @Override
    public int getOrder() {
        // Set order to execute after default filters
        return -1;
    }
}