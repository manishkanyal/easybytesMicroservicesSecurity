package com.eazyBytes.accounts.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

//Record types are the types in which all the fields are final and are initialised only once
@ConfigurationProperties(prefix = "accounts")
@Getter
@Setter
public class AccountsContactInfoDto{
    private String message;
    private Map<String ,String> contactDetails;
    private List<String> onCallSupport;
}
