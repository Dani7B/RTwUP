package redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com ), Gabriele de Capoa, Gabriele Proni
 */
public class RedisStringPublisher implements StringPublisher {

    private final Jedis jedis;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisStringPublisher.class);
    
    public RedisStringPublisher(Jedis jedis) {
        this.jedis = jedis;

    }

    public void publish(String channel, String message) {
        this.jedis.publish(channel, message);
        LOGGER.info("Published. Channel: "+ channel + ", Message: "+ message);
    }
}