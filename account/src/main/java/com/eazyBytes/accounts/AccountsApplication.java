package com.eazyBytes.accounts;

import com.eazyBytes.accounts.dto.AccountsContactInfoDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(value={AccountsContactInfoDto.class})
//This is for Annonotations used in dto package in BaseEntity @LastModifiedBy @CreatedBy auditAwareImpl is a bean
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(info=@Info(title = "Accouts microservices REST API Documentation",
		description = "EasyBanks Accounts microservices REST API Documentation",
		version = "v1",
		contact = @Contact(
				name="Manish Kanyal",
				email="manish.kanyal@gmail.com"
		),
		license = @License(
				name="apache2.0",
				url="https://manish.com/ReadMoreAboutLicence"
		)
	),
		externalDocs = @ExternalDocumentation(
				description = "EasyBytes Accounts Microservices REST API Documentation",
				url="www.dummy.com"
		)
)
public class AccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

}
