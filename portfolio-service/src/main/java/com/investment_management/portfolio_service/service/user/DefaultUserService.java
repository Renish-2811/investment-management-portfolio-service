package com.investment_management.portfolio_service.service.user;

import com.investment_management.portfolio_service.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserService {

    private static final Logger log = LoggerFactory.getLogger(DefaultUserService.class);

    public String getUserInfo(String key) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = (Jwt) authentication.getPrincipal();
            if (key.equals("id")){
                return jwt.getClaimAsString("users_id");
            }
            if(jwt.getClaimAsString(key).isEmpty()){
             throw new ResourceNotFoundException("No value for the given key");
            }
            return jwt.getClaimAsString(key);
    }

    public static String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getTokenValue();
    }
}
