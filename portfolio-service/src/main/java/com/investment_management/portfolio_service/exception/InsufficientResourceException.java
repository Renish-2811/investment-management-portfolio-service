package com.investment_management.portfolio_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class InsufficientResourceException extends RuntimeException{

    public InsufficientResourceException(String message){
        super(message);
    }
}
