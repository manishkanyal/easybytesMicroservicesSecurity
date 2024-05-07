package com.eazyBytes.accounts.services;

import com.eazyBytes.accounts.dto.CustomerDetailsDto;

public interface ICustomerService {

    CustomerDetailsDto fetchCustomerDetails(String mobileNumber,String correlationId);

}
