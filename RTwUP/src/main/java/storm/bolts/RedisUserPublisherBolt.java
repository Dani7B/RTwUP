package storm.bolts;

import java.util.Calendar;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import twitter4j.User;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * This bolt publishes the user to Redis.
 * 
 * @author Daniele Morgantini
 *
 **/

public class RedisUserPublisherBolt extends BaseBasicBolt{

	private static final long serialVersionUID = 1L;
	private JedisPool pool = null;
	private Jedis jedis = null;

	
	@Override
	public void prepare(Map conf, TopologyContext context){
		this.pool = new JedisPool(new JedisPoolConfig(), "localhost");
		this.jedis = this.pool.getResource();
	}
	
	public void execute(Tuple input, BasicOutputCollector collector) {
		String monthKey = Calendar.YEAR + "-" + (Calendar.MONTH + 1);
		String dayKey = monthKey + "-" + Calendar.DAY_OF_MONTH;
		String hourKey = dayKey + "-" + Calendar.HOUR_OF_DAY;
		User user = (User) input.getValueByField("user");
		this.jedis.sadd(hourKey, Long.toString(user.getId()));
		this.jedis.sadd(dayKey, Long.toString(user.getId()));
		this.jedis.sadd(monthKey, Long.toString(user.getId()));
		
		long monthCard = this.jedis.scard(monthKey);
		long dayCard = this.jedis.scard(dayKey);
		long hourCard = this.jedis.scard(hourKey);
		this.jedis.publish("monthCard", Long.toString(monthCard));
		this.jedis.publish("hourCard", Long.toString(hourCard));
		this.jedis.publish("dayCard", Long.toString(dayCard));

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	@Override
	public void cleanup(){
		this.pool.returnResource(this.jedis);
		this.pool.destroy();
	}
}