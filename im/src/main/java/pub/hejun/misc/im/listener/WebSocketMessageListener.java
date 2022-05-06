package pub.hejun.misc.im.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

import java.util.Optional;

/**
 * WebSocket消息监听
 *
 * @author HeJun
 */
@Slf4j
public record WebSocketMessageListener(FluxSink<WebSocketMessage> sink, WebSocketSession session,
                                       RedisSerializer<String> redisSerializer) implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = redisSerializer.deserialize(message.getBody());
        sink.next(session.textMessage(Optional.ofNullable(body).orElse("")));
    }
}
