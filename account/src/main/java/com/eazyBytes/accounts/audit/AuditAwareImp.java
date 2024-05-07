package com.eazyBytes.accounts.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

//This is for Annonotations used in dto package in BaseEntity @LastModifiedBy @CreatedBy
@Component("auditAwareImpl")
public class AuditAwareImp implements AuditorAware<String> {
    @Override
    public Optional getCurrentAuditor() {
        return Optional.of("ACCOUNTS_MS");
    }
}
