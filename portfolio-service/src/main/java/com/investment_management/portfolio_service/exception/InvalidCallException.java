package com.investment_management.portfolio_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidCallException extends RuntimeException {
    public InvalidCallException(String message){
        super(message);
    }
}

