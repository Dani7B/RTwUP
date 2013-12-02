package it.cybion.monitoring.rtwup.storm.bolts;

import java.util.Map;

import it.cybion.monitoring.rtwup.storage.PageDictionary;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
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

public class RedisPublisherBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 8132221751725831364L;

    private JedisPool pool = null;

    private Jedis jedis = null;

	private long topN;

    private PageDictionary counts;
	
	@Override
	public void prepare(final Map conf, final TopologyContext context){
		final String host = (String) conf.get("host");
		this.pool = new JedisPool(new JedisPoolConfig(), host);
		this.jedis = this.pool.getResource();
		this.topN = (Long) conf.get("topN");
		this.counts = PageDictionary.getInstance();
	}

    @Override
    public void execute(final Tuple input, final BasicOutputCollector collector) {
		final String ranking = this.counts.getTopNelementsStringified(this.topN);
		this.jedis.publish("RTwUP", ranking);
	}

    @Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        //
	}

	@Override
	public void cleanup() {
		this.pool.returnResource(this.jedis);
		this.pool.destroy();
	}
}