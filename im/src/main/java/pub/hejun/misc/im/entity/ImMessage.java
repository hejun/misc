package pub.hejun.misc.im.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 消息
 *
 * @author HeJun
 */
@Data
@Accessors(chain = true)
public class ImMessage implements Serializable {

    private String to;
    private String from;
    private String body;
}
