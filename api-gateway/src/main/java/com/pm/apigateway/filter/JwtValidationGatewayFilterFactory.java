package com.pm.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * A GatewayFilterFactory that validates JWT tokens in the Authorization header of incoming requests.
 * <p>
 * This filter checks if the Authorization header contains a Bearer token. If the token is missing or does not
 * start with "Bearer ", it immediately responds with HTTP 401 Unauthorized. If the token is present, it performs
 * a validation by making a GET request to the "/validate" endpoint of an authentication service.
 * <p>
 * If the validation request is successful, the filter allows the request to proceed down the filter chain.
 * Otherwise, it responds with HTTP 401 Unauthorized.
 * <p>
 * The URL of the authentication service is injected via the "auth.service.url" property.
 */

@Component
public class JwtValidationGatewayFilterFactory extends
        AbstractGatewayFilterFactory<Object> {

    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                             @Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    /**
     * Applies the GatewayFilter with the given configuration.
     * This filter checks for the presence of a Bearer token in the Authorization header.
     * If the token is missing or invalid, it responds with HTTP 401 Unauthorized.
     * Otherwise, it validates the token by making a GET request to the "/validate" endpoint
     * and proceeds with the filter chain if validation succeeds.
     *
     * @param config the configuration object (not used in this implementation)
     * @return a GatewayFilter that performs token validation before forwarding the request
     */
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token =
                    exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity()
                    .then(chain.filter(exchange));
        };
    }
}