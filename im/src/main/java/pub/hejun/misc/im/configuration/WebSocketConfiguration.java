package pub.hejun.misc.im.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import pub.hejun.misc.im.handler.MessageHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket配置
 *
 * @author HeJun
 */
@Configuration
public class WebSocketConfiguration {

    @Bean
    public HandlerMapping handlerMapping(MessageHandler messageHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>(8);
        map.put("/ws", messageHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }
}
