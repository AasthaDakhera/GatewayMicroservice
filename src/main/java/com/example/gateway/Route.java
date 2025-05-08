//package com.example.gateway;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class Route {
//
//    @Bean
//    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(p -> p
//                        .path("/sellers/{id}")
//                        .uri("http://localhost:5000/"))
//
//                .route(p -> p
//                        .path("/product/all")
//                        .uri("http://10.20.3.216:8090/"))
//                .build();
//    }
//}
