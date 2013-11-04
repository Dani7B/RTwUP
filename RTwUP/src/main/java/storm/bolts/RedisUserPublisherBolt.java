package storm.bolts;

import it.cybion.model.twitter.User;

import java.util.Calendar;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
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
		final String host = (String) conf.get("redis_host");
		this.pool = new JedisPool(new JedisPoolConfig(), host);
		this.jedis = this.pool.getResource();
	}
	
	public void execute(Tuple input, BasicOutputCollector collector) {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		String monthKey = year + "-" + month;
		String dayKey = monthKey + "-" + day;
		String hourKey = dayKey + "_" + hour;
		User user = (User) input.getValueByField("user_expanded");
		String userID = Long.toString(user.getId());
		Long hUpdated = this.jedis.sadd(hourKey, userID);
		Long dUpdated = this.jedis.sadd(dayKey, userID);
		Long mUpdated = this.jedis.sadd(monthKey, userID);
		
		if(hUpdated == 1)
			this.jedis.publish("active-users-updates", "active-users-hourly");
		if(dUpdated == 1)
			this.jedis.publish("active-users-updates", "active-users-daily");
		if(mUpdated == 1)
			this.jedis.publish("active-users-updates", "active-users-monthly");
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	@Override
	public void cleanup(){
		this.pool.returnResource(this.jedis);
		this.pool.destroy();
	}
}