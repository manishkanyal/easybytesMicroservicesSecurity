package com.eazybytes.cards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Schema(
        name="Accounts",
        description = "Schema to hold  Account Information of Customer"
)
public class AccountsDto {

    @Schema(
            description = "Account Number of EasyBanks Accounts", example = "1234567890"
    )
    @NotEmpty(message = "Account number cannot be empty")
    @Pattern(regexp = "(^$|[0-9]{10})",message = "Account number must be 1o digit numerical value")
    private Long accountNumber;

    @Schema(
            description = "Type Of account" , example = "Saving/Current"
    )
    @NotEmpty(message = "Account type cannot be empty")
    private String accountType;

    @Schema(
            description = "Bank Branch Address", example = "Haldwani, Uttarankhand"
    )
    @NotEmpty(message = "Branch Address cannot be empty")
    private String branchAddress;

}
