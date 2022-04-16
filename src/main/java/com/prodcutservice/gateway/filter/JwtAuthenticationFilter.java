package com.prodcutservice.gateway.filter;

import com.prodcutservice.gateway.exception.JwtTokenMalformedException;
import com.prodcutservice.gateway.exception.JwtTokenMissingException;
import com.prodcutservice.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Component
public class JwtAuthenticationFilter implements GatewayFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();

		//secure api end points needs to be added here
		final List<String> securedEndpoints = new ArrayList<String>();
		securedEndpoints.add("/hello");

		boolean isSecuredApi = securedEndpoints.contains(request.getURI().getPath());

		if (isSecuredApi) {
			if (!request.getHeaders().containsKey("Authorization")) {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				logger.info("UnAuthorized request:"+request.getURI().getPath());
				return response.setComplete();
			}

			final String token = request.getHeaders().getOrEmpty("Authorization").get(0).substring(7);
			try {
				jwtUtil.validateToken(token);
			} catch (JwtTokenMalformedException e) {

				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				logger.error("Unautorized request:"+e.getMessage());
				return response.setComplete();

			} catch (JwtTokenMissingException e){
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.BAD_REQUEST);
				logger.error("Unautorized request:"+e.getMessage());
				return response.setComplete();
			}


	}
		logger.info(("Authorized " + (isSecuredApi?"Secured Request":"Non Secured Request ")+":"+request.getURI().getPath()));
		return chain.filter(exchange);
	}

}
