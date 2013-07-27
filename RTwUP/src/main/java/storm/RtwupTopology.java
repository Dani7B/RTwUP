package storm;

import storm.bolts.ExpanderBolt; 
import storm.bolts.RedisPublisherBolt;
import storm.bolts.URLCounterBolt;
import storm.spouts.TwitterSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.utils.*;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

/**
 * @author Gabriele Proni, Gabriele de Capoa, Daniele Morgantini
 * 
 */
public class RtwupTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("filteredStream", new TwitterSpout(), 1);
		builder.setBolt("expander", new ExpanderBolt(), 5).shuffleGrouping(
				"filteredStream");
		builder.setBolt("urlCounter", new URLCounterBolt(), 5).fieldsGrouping(
				"expander", new Fields("expanded_url_domain"));
		builder.setBolt("RedisPublisher", new RedisPublisherBolt(), 1).shuffleGrouping(
				"urlCounter");

		Config conf = new Config();
		conf.setDebug(true);
		
		if (args != null && args.length > 0) {

			conf.setNumWorkers(3);

			conf.put("topN", Integer.parseInt(args[1])); //assuming that topN is the second argument
			
			try {
				StormSubmitter.submitTopology(args[0], conf,
						builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		} else {
			conf.put("topN", 10);
			try{
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("RTwUP", conf, builder.createTopology());
			Utils.sleep(300000);
			cluster.killTopology("RTwUP");
			cluster.shutdown();
			}catch (Exception e){
				System.err.println("Error");
				e.printStackTrace();
			}
		}

	}
}
