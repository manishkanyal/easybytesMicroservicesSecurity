package com.eazybytes.gatewayServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    //This is for only authenticating without roles
//    @Bean
//    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity security)
//    {
//        security.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers(HttpMethod.GET).permitAll()
//                        .pathMatchers("/eazybanks/accounts/**").hasRole("ACCOUNTS")
//                        .pathMatchers("/eazybanks/cards/**").hasRole("CARDS")
//                        .pathMatchers("/eazybanks/loans/**").hasRole("LOANS"))
//                //Below config we are making our gateway server as resource server
//                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
//                        .jwt(Customizer.withDefaults()));
//
//        //We are disabling here because csrf is needed when browser are invovlved
//        security.csrf(csrfSpec -> csrfSpec.disable());
//        return security.build();
//    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity security)
    {
        security.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers(HttpMethod.GET).permitAll()
                .pathMatchers("/eazybanks/accounts/**").hasRole("ACCOUNTS")
                .pathMatchers("/eazybanks/cards/**").hasRole("CARDS")
                .pathMatchers("/eazybanks/loans/**").hasRole("LOANS"))
                //Below config we are making our gatewar server as resource server
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));

        security.csrf(csrfSpec -> csrfSpec.disable());  ////We are disabling here because csrf is needed when browser are invovlved
        return security.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter
                (new KeyCloackRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }




}
