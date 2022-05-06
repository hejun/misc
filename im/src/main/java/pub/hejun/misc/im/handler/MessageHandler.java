package pub.hejun.misc.im.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import pub.hejun.misc.im.entity.ImMessage;
import pub.hejun.misc.im.listener.WebSocketMessageListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理
 *
 * @author HeJun
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler implements WebSocketHandler {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String TOPIC_PREFIX = "_im_message_";

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("获取链接, Session: {}", session.getId());

        PatternTopic patternTopic = new PatternTopic(String.join(TOPIC_PREFIX, session.getId()));
        List<MessageListener> tempContainer = new ArrayList<>();

        return session
                .send(Flux.create(sink -> {
                    tempContainer.add(new WebSocketMessageListener(sink, session, redisTemplate.getStringSerializer()));
                    redisMessageListenerContainer.addMessageListener(tempContainer.get(0), patternTopic);
                    session
                            .receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .map(message -> this.parserMessage(message, session.getId()))
                            .doOnNext(this::sendMessage)
                            .subscribe();
                }))
                .doFinally(signalType -> {
                    log.info("断开链接, 用户：{}, Session: {}", session.getId(), session.getId());
                    redisMessageListenerContainer.removeMessageListener(tempContainer.get(0), patternTopic);
                    session.close(CloseStatus.NORMAL);
                });
    }

    private ImMessage parserMessage(String messageJson, String from) {
        try {
            return objectMapper.readValue(messageJson, ImMessage.class).setFrom(from);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(ImMessage message) {
        String string;
        try {
            string = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(String.join(TOPIC_PREFIX, message.getTo()), string);
    }
}
