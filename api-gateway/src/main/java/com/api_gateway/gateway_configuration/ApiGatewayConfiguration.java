package com.api_gateway.gateway_configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {
	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		
		return builder.routes()
				.route("currncy-exchange", r -> r.path("/currency-exchange/**")
						.uri("lb://CURRENCY-EXCHANGE"))
				.route("currncy-exchange", r -> r.path("/resillience4j/**")
						.uri("lb://CURRENCY-EXCHANGE"))
				.route("currency_conversion", r -> r.path("/currency_conversion/**")
					.uri("lb://CURRENCY-CONVERSION"))
				.build();
	}
	
}
