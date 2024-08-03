package com.investment_management.portfolio_service.service.notification;

import reactor.core.publisher.Flux;

public interface NotificationService {

    Flux<String> getNotificationStream(final String id);
}
