package com.eazyBytes.accounts.controllers;


import com.eazyBytes.accounts.dto.CustomerDetailsDto;
import com.eazyBytes.accounts.dto.ErrorResponseDto;
import com.eazyBytes.accounts.services.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "REST APIS for Customers in EasyBanks",
        description = "REST APIS in EasyBanks to FETCH customer details"
)
@RestController
//produces = {MediaType.APPLICATION_JSON_VALUE}: This part of the annotation specifies the media types that the method can produce and send back to the client. In this case,
//        it indicates that the method produces JSON responses. MediaType.APPLICATION_JSON_VALUE is a constant defined in Spring that represents the MIME type for JSON
//        (application/json).
@RequestMapping(path="/api",produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated          //This annonotaion tells our controller to perform validation

public class CustomerController {

    private Logger logger= LoggerFactory.getLogger(CustomerController.class);

    private final ICustomerService iCustomerService;

    public CustomerController(ICustomerService iCustomerService) {
        this.iCustomerService = iCustomerService;
    }

    @Operation(
            summary = "Fetch Customer Details using REST API ",
            description = "REST API to fetch customer details using mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP STATUS OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP STATUS Internal Server Error",

                    //THis is used because to bring ErrorResponseDto class in swagger documentation. Remove this you will not be able to see
                    //ErrorResponse in Documentation. Here we are telling ErrorResponse also exists to swagger
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })


    @GetMapping("/fetchCustomerDetails")
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(@RequestHeader("eazybank-correlation-id")
                                                                       String correlationId,
                                                                   @RequestParam @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
                                                                   String mobileNumber){

        logger.debug("eazyBanks-correlation-id found: {}",correlationId);
        CustomerDetailsDto customerDetailsDto= iCustomerService.fetchCustomerDetails(mobileNumber,correlationId);
        return ResponseEntity.status(HttpStatus.OK).body(customerDetailsDto);

    }

}
