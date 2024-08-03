package com.investment_management.portfolio_service.controller;

import com.investment_management.portfolio_service.service.notification.NotificationService;
import com.investment_management.portfolio_service.service.user.DefaultUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequestMapping("/api/v1")
@RestController
public class NotificationController {

    @Autowired
    private NotificationService defaultNotificationService;

    @Autowired
    private DefaultUserService defaultUserService;


    @GetMapping(path = "/notification/all")
    public Flux<ServerSentEvent<Object>> subscribe(){
        return defaultNotificationService.getNotificationStream(defaultUserService.getUserInfo("id"))
                .map(data -> ServerSentEvent.builder()
                        .id(UUID.randomUUID().toString())
                        .comment("Notification Event")
                        .data(data).event("notification-event").build());
    }
}
