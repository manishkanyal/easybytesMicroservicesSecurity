package com.eazyBytes.accounts.services.impl;

import com.eazyBytes.accounts.dto.*;
import com.eazyBytes.accounts.entity.Accounts;
import com.eazyBytes.accounts.entity.Customer;
import com.eazyBytes.accounts.exception.ResourceNotFoundException;
import com.eazyBytes.accounts.mapper.AccountsMapper;
import com.eazyBytes.accounts.mapper.CustomerMapper;
import com.eazyBytes.accounts.repository.AccountsRepository;
import com.eazyBytes.accounts.repository.CustomerRepository;
import com.eazyBytes.accounts.services.ICustomerService;
import com.eazyBytes.accounts.services.client.CardsFeignClient;
import com.eazyBytes.accounts.services.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountsRepository accountsRepository;

    private CustomerRepository customerRepository;

    private CardsFeignClient cardsFeignClient;

    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber,String correlationId) {



        Customer customer= customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Accounts accounts=accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(()->new ResourceNotFoundException("Account","CustomerId",customer.getCustomerId().toString()));

        CustomerDto customerDto= CustomerMapper.mapToCustomerDto(customer,new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts,new AccountsDto()));

        CustomerDetailsDto customerDetailsDto=CustomerMapper.mapToCustomerDetailsDto(customer,new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts,new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity= loansFeignClient.fetchLoanDetails(correlationId,mobileNumber);
        if(loansDtoResponseEntity!=null)
        {
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        }

        ResponseEntity<CardsDto> cardsDtoResponseEntity= cardsFeignClient.fetchCardDetails(correlationId,mobileNumber);
        if(cardsDtoResponseEntity!=null)
        {
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }
        return customerDetailsDto;
    }
}
