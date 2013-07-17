package storm.bolts;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import storage.URLMap;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * This bolt counts the URL.
 * 
 * @author Gabriele de Capoa, Gabriele Proni, Daniele Morgantini
 * 
 */

public class URLCounterBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	private ConcurrentSkipListMap<String, Map<String, Integer>> counts;
	private JedisPool pool = null;
	private Jedis jedis = null;
	
	public void prepare(Map conf, TopologyContext context) {
		this.counts = URLMap.getInstance();
		
		this.pool = new JedisPool(new JedisPoolConfig(), "localhost");
		this.jedis = pool.getResource();
	}

	public void execute(Tuple input, BasicOutputCollector collector) {

		String domain = input.getStringByField("expanded_url_domain");
		String path = input.getStringByField("expanded_url_complete");
		Integer count = 1;
		Map<String, Integer> ranking = null;

		ranking = this.counts.get(domain);
		if (ranking == null)
			ranking = new HashMap<String, Integer>();
		else {
			count = ranking.get(path);
			if (count == null)
				count = 1;
			else
				count++;
		}
		ranking.put(path, count);
		this.counts.put(domain, ranking);
		
		this.jedis.publish("RTWUP.domain", domain);
		this.jedis.publish("RTWUP.url", path);
		this.jedis.publish("RTWUP.count", count.toString());
		
		System.out.println("Domain: " + domain + " URL: " + path + " Count: "
				+ count);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	public void cleanup(){
		this.pool.returnResource(this.jedis);
		this.pool.destroy();
	}
	
}