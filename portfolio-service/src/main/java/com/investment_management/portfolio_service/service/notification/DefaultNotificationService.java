package com.investment_management.portfolio_service.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class DefaultNotificationService implements NotificationService {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private Map<String, AbstractMessageListenerContainer> containers;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Flux<String> getNotificationStream(String id) {
        declareAndBindQueue(id);
        final var eventEmmitter= new DefaultPushNotificationEmitter(id);
        final var listner  = new MessageListenerAdapter();
        listner.setDelegate(eventEmmitter);
        listner.setDefaultListenerMethod("emit");
        listner.setMessageConverter(new SimpleMessageConverter());

        final var container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("subscription."+id);
        container.setMessageListener(listner);

        final var containerKey=id+"-"+ UUID.randomUUID();
        containers.put(containerKey,container);
        container.start();
        return Flux.create(eventEmmitter).doOnCancel(()->{
            log.info("Connection {} is being close",containerKey);
            containers.get(containerKey).stop();
            containers.remove(containerKey);
        });
    }

    private void declareAndBindQueue(final String id){
        final var queue = new Queue("subscription."+id);
        final var exchange = ExchangeBuilder.fanoutExchange("notify.all")
                .durable(true).build();
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("").noargs());

    }
}
