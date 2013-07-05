package storm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import twitter4j.GeoLocation;
import twitter4j.Status;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class FilterBolt extends BaseRichBolt {


	private static final long serialVersionUID = 1L;

	private OutputCollector collector;
	
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector=collector;

	}

	public void execute(Tuple input) {
		Status status = (Status) input.getValue(0);
		GeoLocation geoloc = status.getGeoLocation();
		if (geoloc.getLongitude()>=12.38 && geoloc.getLongitude()<=12.68 && geoloc.getLatitude()>=41.80 && geoloc.getLatitude()<=42.00){
			List<Object> tuple = new ArrayList<Object>();
			tuple.add(input);
			this.collector.emit(tuple);
			this.collector.ack(input);
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet_filtered"));
	}

}
