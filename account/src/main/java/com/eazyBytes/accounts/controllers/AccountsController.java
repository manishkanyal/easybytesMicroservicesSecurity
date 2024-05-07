package com.eazyBytes.accounts.controllers;

import com.eazyBytes.accounts.constants.AccountsConstants;
import com.eazyBytes.accounts.dto.AccountsContactInfoDto;
import com.eazyBytes.accounts.dto.CustomerDto;
import com.eazyBytes.accounts.dto.ErrorResponseDto;
import com.eazyBytes.accounts.dto.ResponseDto;
import com.eazyBytes.accounts.entity.Accounts;
import com.eazyBytes.accounts.services.IAccountsService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

@RestController
//produces = {MediaType.APPLICATION_JSON_VALUE}: This part of the annotation specifies the media types that the method can produce and send back to the client. In this case,
//        it indicates that the method produces JSON responses. MediaType.APPLICATION_JSON_VALUE is a constant defined in Spring that represents the MIME type for JSON
//        (application/json).
@RequestMapping(path="/api",produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated          //This annonotaion tells our controller to perform validation
@Tag(
        name = "CRUD REST APIS for Accounts in EasyBanks",
        description = "CRUD REST APIS for Accounts in EasyBanks to CREATE , READ , UPDATE , DELETE accounts details"
)
public class AccountsController {

    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    private final IAccountsService iAccountsService;

    //Here i am not using lombok for constructor is due to the String buildVersion . When i am making constructor with lombok, it is
    //able to find bean of String buildVersion(because it does not exist as bean only as value)
    public AccountsController(IAccountsService iAccountsService) {
        this.iAccountsService = iAccountsService;
    }

    //First way to read properties
    @Value("${build.version}")
    private String buildVersion;

    //Second way to read properties
    @Autowired
    private Environment environment;

    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;

    @Operation(
            summary = "Create Accounts using REST API ",
            description = "REST API to create new customer and accounts inside EasyBanks"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP STATUS CREATED"
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto)
    {
        iAccountsService.createAccount(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201,AccountsConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Accounts Details using REST API ",
            description = "REST API to fetch customer and accounts details using mobile number"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP STATUS OK"
    )
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccountsDetails(@RequestHeader("eazybank-correlation-id") String correlationId,
                    @RequestParam  @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
                    String mobileNumber)
    {
        logger.debug("eazyBanks-correlation-id found: {}",correlationId);
        CustomerDto customerDto= iAccountsService.fetchAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }

    @Operation(
            summary = "Update Accounts Details using REST API ",
            description = "REST API to update customer and accounts details using CustomerDTO"
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
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Exception Failed"
            )
    })

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDto customerDto) {
        boolean isUpdated = iAccountsService.updateAccount(customerDto);
        if(isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_UPDATE));
        }
    }


    @Operation(
            summary = "DELETE Accounts Details using REST API ",
            description = "REST API to fetch customer and accounts details using mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP STATUS OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP STATUS Internal Server Error"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Exception Failed"
            )}
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
                                                                String mobileNumber) {
        boolean isDeleted = iAccountsService.deleteAccount(mobileNumber);
        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
        }
    }


    @Operation(
            summary = "Get Build Information ",
            description = "Get Build Information that is deployed in accounts microservices"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP STATUS OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP STATUS Internal Server Error"
            )}
    )
    //Retry pattern in resiliency
    @Retry(name = "getBuildInfo",fallbackMethod = "getBuildInfoFallback")
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() throws TimeoutException {
        logger.debug("getBuildInfo method invoked!");
//        throw new TimeoutException();
        //throw new NullPointerException();
        return ResponseEntity.status(HttpStatus.OK)
                .body(buildVersion);
    }

    //In gateway server we do not have any mechanism to set fallback method after the retries have been failed
    //THis fallback method run when the all the retries fails on orignial method(getBuildInfo())
    //Fallback method should have all the original method signatures (Parameter passed)
    //Fallback method should accept Throwable parameter
    public ResponseEntity<String> getBuildInfoFallback(Throwable throwable)
    {
        logger.debug("getBuildInfoFallback method invoked!");
        return ResponseEntity.status(HttpStatus.OK).body("Fallback method ");

    }

    @Operation(
            summary = "Get JAVA Version Information ",
            description = "Get JAVA Version Information that is installed in accounts microservices"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP STATUS OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP STATUS Internal Server Error"
            )}
    )
    @RateLimiter(name = "getJavaVersion",fallbackMethod = "getJavaVersionFallback")
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion()
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }

    public ResponseEntity<String> getJavaVersionFallback(Throwable throwable)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body("Java 17");
    }


    @Operation(
            summary = "Get Contact Information ",
            description = "Contact Information  Details to reach if any issue arises"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP STATUS OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP STATUS Internal Server Error"
            )}
    )
    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo()
    {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactInfoDto);
    }
}
