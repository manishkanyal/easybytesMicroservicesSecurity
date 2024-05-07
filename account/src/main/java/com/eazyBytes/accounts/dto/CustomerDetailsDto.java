package com.eazyBytes.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Schema(
        name="CustomerDetails",
        description = "Schema to hold Customer ,Cards , Loans and Account Information"
)
@Data
public class CustomerDetailsDto {

    @Schema(
            description = "Name of Customer" , example = "EasyBytes"
    )
    @NotEmpty(message = "Name cannot be empty")
    @Size(min=5,max = 30,message = "Name should be between 5 to 30 character")
    private String name;

    @Schema(
            description = "Email of Customer" , example = "tutoe@EasyBytes.com"
    )
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Schema(
            description = "Mobile Number of Customer" , example = "9999999999"
    )
    @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Schema(
            description = "Account Details of customer"
    )
    private AccountsDto accountsDto;

    @Schema(
            description = "Account Details of customer"
    )
    private LoansDto loansDto;

    @Schema(
            description = "Account Details of customer"
    )
    private CardsDto cardsDto;




}
