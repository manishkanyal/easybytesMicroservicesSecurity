package com.eazybytes.gatewayServer.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


//Configuring fallback . Fallback is mechanism , what to do when the circuitbreaker is open or request is being processed , we simply cannot send error to client
//So we send a customised message to client here.
@RestController
public class FallbackController {


    @RequestMapping("/contactSupport")
    public Mono<String> contactSupport()
    {
        return Mono.just("An error occured. Please try after sometime or contact the support team");
    }

}
