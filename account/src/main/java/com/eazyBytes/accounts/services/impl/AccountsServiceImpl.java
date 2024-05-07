package com.eazyBytes.accounts.services.impl;

import com.eazyBytes.accounts.constants.AccountsConstants;
import com.eazyBytes.accounts.dto.AccountsDto;
import com.eazyBytes.accounts.dto.CustomerDto;
import com.eazyBytes.accounts.entity.Accounts;
import com.eazyBytes.accounts.entity.Customer;
import com.eazyBytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazyBytes.accounts.exception.ResourceNotFoundException;
import com.eazyBytes.accounts.mapper.AccountsMapper;
import com.eazyBytes.accounts.mapper.CustomerMapper;
import com.eazyBytes.accounts.repository.AccountsRepository;
import com.eazyBytes.accounts.repository.CustomerRepository;
import com.eazyBytes.accounts.services.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {


    AccountsRepository accountsRepository;

    CustomerRepository customerRepository;

    @Override
    public void createAccount(CustomerDto customerDto) {

        Customer customer= CustomerMapper.mapToCustomer(customerDto,new Customer());

        Optional<Customer> optionalCustomer=customerRepository.findByMobileNumber(customer.getMobileNumber());
        if(optionalCustomer.isPresent())
        {
            throw new CustomerAlreadyExistsException("Customer with given mobile number already Exists: "+customer.getMobileNumber());
        }

        //This is being handled by JpaAuditing feature of JPA using AuditAware

//        customer.setCreatedAt(LocalDateTime.now());
//        customer.setCreatedBy(customer.getName());
        Customer savedCustomer= customerRepository.save(customer);

        accountsRepository.save(createNewAccount(savedCustomer));
    }

    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
//        newAccount.setCreatedAt(LocalDateTime.now());
//        newAccount.setCreatedBy(customer.getName());
        return newAccount;
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {

        Customer customer= customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Accounts accounts=accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(()->new ResourceNotFoundException("Account","CustomerId",customer.getCustomerId().toString()));

        CustomerDto customerDto=CustomerMapper.mapToCustomerDto(customer,new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts,new AccountsDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {

        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto !=null ){
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", accountsDto.getAccountNumber().toString())
            );
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto,customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return  isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {


        Customer customer=customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()-> new ResourceNotFoundException("Customer","Mobile Number",mobileNumber));


        Accounts accounts=accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                ()-> new ResourceNotFoundException("Account","Customer Id",customer.getCustomerId().toString()));

        accountsRepository.deleteByCustomerId(accounts.getCustomerId());
        customerRepository.delete(customer);
        return true;

    }

}
