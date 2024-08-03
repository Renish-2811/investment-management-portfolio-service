package com.investment_management.portfolio_service.helper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.sql.Time;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogHandlerInterceptor extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().contains("/api")){
            long time = System.currentTimeMillis();
            try {
                filterChain.doFilter(request,response);
            }
            finally {
                time = System.currentTimeMillis()-time;
                log.info("{} {} {} {} {} ms",request.getMethod(),request.getRequestURI(),
                        response.getContentType(),
                        response.getStatus(),
                        time);
            }
        }
        else {
            filterChain.doFilter(request,response);
        }

    }
}
