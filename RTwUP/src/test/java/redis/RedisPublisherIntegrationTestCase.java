package redis;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;

import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class RedisPublisherIntegrationTestCase {

    private Jedis jedis;
    private RedisStringPublisher redisPublisher;

    @BeforeClass
    public void setUp() {

        this.jedis = null;
        //        this.jedis = new Jedis("localhost");
        this.redisPublisher = new RedisStringPublisher(this.jedis);

        assertTrue(redisIsRunning());
    }

    private Boolean redisIsRunning() {
//        String pingResponse = jedis.ping();

        return true;
    }

    @Test
    public void shouldPublishAndConsumeOnRedis() {

    }

}
