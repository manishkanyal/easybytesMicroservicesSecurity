package com.eazybytes.gatewayServer;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServerApplication.class, args);
	}

	@Bean
	public RouteLocator eazyBankRouteConfig(RouteLocatorBuilder routeLocatorBuilder)
	{
		return routeLocatorBuilder.routes()
				.route(path-> path
						.path("/eazybank/accounts/**")
						.filters(filter-> filter.rewritePath("/eazybank/accounts/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config.setName("accountsCircuitBreaker")
										.setFallbackUri("forward:/contactSupport"))
						)
						.uri("lb://ACCOUNTS"))

				.route(path-> path
						.path("/eazybank/cards/**")
						.filters(filter-> filter.rewritePath("/eazybank/cards/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
										.requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
												.setKeyResolver(userKeyResolver()))

								)

						.uri("lb://CARDS"))

				.route(path-> path
						.path("/eazybank/loans/**")
						.filters(filter-> filter.rewritePath("/eazybank/loans/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								//Here we are setting how many retries should be there when request is facing issues
								//But in gateway server we cannot setup fallback method after the retries fails
								.retry(retryConfig -> retryConfig.setRetries(3)
										//Setting which HTTP Method retries should be applied to
										.setMethods(HttpMethod.GET)
										//for 1 retry wait 100ms. Then after this if request again fails then the next retry time will be higher then before .
										//Duration.ofMillis(1000) is for maxBackoff time . It tells for next retries our spring cloud will wait only for max of 1000ms
										//2 is Factor value that our spring cloud will apply for when the retries fails
										//True - indicates that to apply all the values like factor value , max backoff value.
										.setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true)))
						.uri("lb://LOANS"))
				.build();
	}

	//This is bean for changing default configuration of circuit breaker
	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
				//TimeLimiterConfig helps to define that max time our app will take to complete the request
				//Default value in timeout is 1 sec , here we have set it to 4 sec
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
	}

//This bean is for Rate Limiter
	@Bean
	public RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(1, 1, 1);
	}

	//This bean is for Rate Limiter
	@Bean
	KeyResolver userKeyResolver() {
		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
				.defaultIfEmpty("anonymous");
	}

}
