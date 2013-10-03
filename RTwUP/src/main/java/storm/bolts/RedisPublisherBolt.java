package storm.bolts;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import storage.PageDictionary;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * This bolt publishes the URL ranking to Redis.
 * 
 * @author Gabriele de Capoa, Gabriele Proni, Daniele Morgantini
 *
 **/

public class RedisPublisherBolt extends BaseBasicBolt{

	private static final long serialVersionUID = 1L;
	private JedisPool pool = null;
	private Jedis jedis = null;

	private long topN;
	private PageDictionary counts;
	
	@Override
	public void prepare(Map conf, TopologyContext context){
		String host = (String) conf.get("host");
		this.pool = new JedisPool(new JedisPoolConfig(), host);
		this.jedis = this.pool.getResource();
		this.topN = (Long) conf.get("topN");
		this.counts = PageDictionary.getInstance();
	}
	
	public void execute(Tuple input, BasicOutputCollector collector) {
		String ranking = this.counts.getTopNelementsStringified(this.topN);
		this.jedis.publish("RTwUP", ranking);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	@Override
	public void cleanup(){
		this.pool.returnResource(this.jedis);
		this.pool.destroy();
	}
}