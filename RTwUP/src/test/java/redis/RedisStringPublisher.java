package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class RedisStringPublisher implements StringPublisher {

    private final Jedis jedis;

    public RedisStringPublisher(Jedis jedis) {
        this.jedis = jedis;

    }

    public void publish(String channel, String message) {
        Long value = this.jedis.publish(channel, message);
    }
}
