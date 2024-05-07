package com.eazyBytes.accounts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus will always send the BAD_REQUEST status to client
@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(String message){
        super(message);
    }

}
