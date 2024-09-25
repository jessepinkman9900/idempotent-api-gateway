package com.poc.api_gateway.configs;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class Routes {

    // read routing details from application.properties on startup before gateway server live
    @Value("${api-gateway.routes.service1.source.path}")
    private String service1SourcePath;

    @Value("${api-gateway.routes.service1.target.uri}")
    private String service1TargetUri;

    @Value("${api-gateway.routes.service1.target.path}")
    private String service1TargetPath;


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // route 1
            .route("route1", r -> {
                return r.path(service1SourcePath)
                    .and().method(HttpMethod.GET)
                    .filters(f -> f.rewritePath(service1SourcePath, service1TargetPath))
                    .uri(service1TargetUri);
            })
            .build();
    }        
    
}
