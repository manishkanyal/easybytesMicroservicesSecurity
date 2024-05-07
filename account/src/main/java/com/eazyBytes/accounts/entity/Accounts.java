package com.eazyBytes.accounts.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Accounts extends BaseEntity{


    @Column(name="customer_id")
    private Long customerId;

    @Id
    @Column(name="account_number")
    private Long accountNumber;

    private String accountType;

    private String branchAddress;
}
