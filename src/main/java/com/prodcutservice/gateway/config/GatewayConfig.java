package com.prodcutservice.gateway.config;

import com.prodcutservice.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	@Autowired
	private JwtAuthenticationFilter filter;

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes().route("signin", r -> r.path("/signin/**").filters(f -> f.filter(filter)).uri("http://localhost:8087/signin"))
				.route("signup", r -> r.path("/signup/**").filters(f -> f.filter(filter)).uri("http://localhost:8087/signup"))
				.route("hello", r -> r.path("/hello/**").filters(f -> f.filter(filter)).uri("http://localhost:8087/hello"))
				.route("product-service", r -> r.path("/product/**").filters(f -> f.filter(filter)).uri("http://localhost:8090"))
				.build();
	}

}
