package com.example.gateway.filters;

import com.example.gateway.Services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerWebExchange mutedExchange = exchange.mutate().request(exchange.getRequest()).build();
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (exchange.getRequest().getURI().getPath().contains("/auth/login") ||
                exchange.getRequest().getURI().getPath().contains("/auth/signup") ||
        exchange.getRequest().getURI().getPath().contains("/product") ||
        exchange.getRequest().getURI().getPath().contains("/search")){
            return chain.filter(exchange);
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtService.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtService.getUserIdFromToken(token);
        String useremail = jwtService.getUserEmailFromToken(token);

        System.out.println("Extracted userId: " + username);


        System.out.println("Request Headers Before Mutation: " + exchange.getRequest().getHeaders());

        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .header("username", username)
                .header("useremail",useremail)
                .build();

        System.out.println("Request Headers After Mutation: " + modifiedRequest.getHeaders());

        return chain.filter(mutedExchange.mutate().request(modifiedRequest).build());
    }
//
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getURI().getPath();
//        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//        // Allow access to specific endpoints without authentication
//        if (path.contains("/auth/login") || path.contains("/auth/signup") || path.contains("/product")) {
//            return chain.filter(exchange);
//        }
//
//        // Check if the Authorization header is missing or does not start with "Bearer "
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        // Extract the token and validate it reactively
//        String token = authHeader.substring(7);
//
//        return Mono.just(jwtService.validateToken(token))
//                .flatMap(isValid -> {
//                    if (!isValid) {
//                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                        return exchange.getResponse().setComplete();
//                    }
//
//                    // If the token is valid, extract user ID reactively
//                    return Mono.just(jwtService.getUserIdFromToken(token))
//                            .flatMap(userId -> {
//                                System.out.println("Extracted userId: " + userId);
//
//                                // Mutate the request with new headers
//                                ServerHttpRequest modifiedRequest = request.mutate()
//                                        .header(HttpHeaders.AUTHORIZATION, authHeader)
//                                        .header("X-USER-ID", userId)
//                                        .build();
//
//                                ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
//
//                                System.out.println("Request Headers After Mutation: " + modifiedRequest.getHeaders());
//
//                                // Continue with the filter chain using the modified exchange
//                                return chain.filter(modifiedExchange);
//                            });
//                });
//    }

    @Override
    public int getOrder() {
        return -1;
    }
}
