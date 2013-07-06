package storm;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * This bolt counts the URL.
 * 
 * @author Gabriele de Capoa, Gabriele Proni
 *
 */

public class URLCounterBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	private Map<String, Integer> counts = new HashMap<String, Integer>();
	
	public void execute(Tuple input, BasicOutputCollector collector) {
		
		String url = input.getStringByField("expanded_url");
		Integer count = this.counts.get(url);
		
		if(count==null)
			count = 0;
		count++;
		this.counts.put(url, count);
		
		collector.emit(new Values (url, count));

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		declarer.declare(new Fields("domain", "count"));

	}

}
