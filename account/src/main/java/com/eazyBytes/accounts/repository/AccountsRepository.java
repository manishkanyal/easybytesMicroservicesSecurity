package com.eazyBytes.accounts.repository;

import com.eazyBytes.accounts.entity.Accounts;
import com.eazyBytes.accounts.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts,Long> {

    Optional<Accounts> findByCustomerId(Long customerId);

    //@Transactional, Spring Boot ensures that the method runs within a transaction. If the method completes successfully, the transaction
    // commits, and if an exception occurs, the transaction rolls back.
    //The @Modifying annotation is used to indicate that a method modifies the database state.
    @Transactional
    @Modifying
    void deleteByCustomerId(Long customerId);

}
