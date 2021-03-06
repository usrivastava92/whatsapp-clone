package app.whatsapp.commonweb.config;

import app.whatsapp.commonweb.hooks.MqMessageReceiver;
import app.whatsapp.commonweb.properties.MqConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "application.mq.enable", havingValue = "true")
public class MqConfig {

    private final MqConfigProperties mqConfigProperties;

    public MqConfig(MqConfigProperties mqConfigProperties) {
        this.mqConfigProperties = mqConfigProperties;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        log.info("Initializing MqConnectionFactory");
        CachingConnectionFactory cachingConnectionFactory = null;
        if (StringUtils.isBlank(mqConfigProperties.getHostname())) {
            cachingConnectionFactory = new CachingConnectionFactory();
        } else if (mqConfigProperties.getPort() == null) {
            cachingConnectionFactory = new CachingConnectionFactory(mqConfigProperties.getHostname());
        } else {
            cachingConnectionFactory = new CachingConnectionFactory(mqConfigProperties.getHostname(), mqConfigProperties.getPort());
        }
        if (StringUtils.isNotBlank(mqConfigProperties.getUri())) {
            cachingConnectionFactory.setUri(mqConfigProperties.getUri());
        } else {
            if (StringUtils.isNoneBlank(mqConfigProperties.getVHost())) {
                cachingConnectionFactory.setVirtualHost(mqConfigProperties.getVHost());
            }
            cachingConnectionFactory.setUsername(mqConfigProperties.getUsername());
            cachingConnectionFactory.setUsername(mqConfigProperties.getPassword());
        }
        if (mqConfigProperties.getConnectionLimit() > 0) {
            cachingConnectionFactory.setConnectionLimit(mqConfigProperties.getConnectionLimit());
        }
        return cachingConnectionFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(MqMessageReceiver receiver) {
        return new MessageListenerAdapter(receiver, "handleMessage");
    }

}
