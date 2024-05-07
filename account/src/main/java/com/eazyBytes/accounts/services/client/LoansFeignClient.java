package com.eazyBytes.accounts.services.client;

import com.eazyBytes.accounts.dto.LoansDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

//This Interface will be used by Account microservices to communicate with cards microservice

@FeignClient(value = "loans",fallback = LoansFallback.class)
public interface LoansFeignClient {

    //This method signature should be same as defined in cards controller because it will use this method to communicate to cards microservice

    @GetMapping(value = "/api/fetch",consumes = "application/json")
    public ResponseEntity<LoansDto> fetchLoanDetails(@RequestHeader("eazybank-correlation-id") String correlationId, @RequestParam String mobileNumber);

}
