package storm.bolts;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; 

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

<<<<<<< HEAD
	private ConcurrentHashMap<String, Map<String, Integer>> counts; 
	
	public void prepare(Map conf, TopologyContext context){
		this.counts=URLMap.getInstance();
=======
	private ConcurrentHashMap<String, Map<String, Integer>> counts;

	public void prepare(Map conf, TopologyContext context) {
		this.counts = URLMap.getInstance();
>>>>>>> 514e4c69113d0e36a4f3606f85d605f9f2e6e105
	}

	public void execute(Tuple input, BasicOutputCollector collector) {

		String domain = input.getStringByField("expanded_url_domain");
		String file = input.getStringByField("expanded_url_file");
		Integer count = 1;
		Map<String, Integer> ranking = null;
<<<<<<< HEAD
		
		ranking = this.counts.get(domain);
		if(ranking == null)
			ranking = new HashMap<String, Integer>();
		else {
			count = ranking.get(file);
			if(count == null)
=======

		ranking = this.counts.get(domain);
		if (ranking == null) {
			ranking = new HashMap<String, Integer>();
		} else {
			count = ranking.get(file);
			if (count == null)
>>>>>>> 514e4c69113d0e36a4f3606f85d605f9f2e6e105
				count = 1;
			else
				count++;
		}
		ranking.put(file, count);
		this.counts.put(domain, ranking);
<<<<<<< HEAD
		System.out.println("Domain: " + domain + " File: "+ file+ " Count: " + count); 
=======
		
		System.err.println("Domain: " + domain + " File: " + file + " Count: "
				+ count);
>>>>>>> 514e4c69113d0e36a4f3606f85d605f9f2e6e105
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
