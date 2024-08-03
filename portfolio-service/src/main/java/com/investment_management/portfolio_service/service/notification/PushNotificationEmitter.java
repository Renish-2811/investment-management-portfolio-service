package com.investment_management.portfolio_service.service.notification;

import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

public interface PushNotificationEmitter<T> extends Consumer<FluxSink<T>> {

    /**
     * Emit.
     *
     * @param event the event
     */
    void emit(final T event);

    /**
     * Gets id.
     *
     * @return the id
     */
    String getId();
}
