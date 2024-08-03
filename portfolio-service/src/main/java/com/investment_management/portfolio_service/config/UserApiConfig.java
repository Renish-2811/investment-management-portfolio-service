package com.investment_management.portfolio_service.config;

import com.investment_management.portfolio_service.api.BankApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class UserApiConfig extends BaseApiConfig{

        @Bean
        public BankApi bankApi(){
        return createService(BankApi.class);
    }

}
