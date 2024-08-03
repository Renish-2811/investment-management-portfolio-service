package com.investment_management.portfolio_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FOUND)
public class ResourceAlreadyFoundException extends RuntimeException {
    public ResourceAlreadyFoundException(String message){
        super(message);
    }
}
