package storm;

import storm.bolts.ExpanderUserURLBolt;
import storm.bolts.RedisUserPublisherBolt;
import storm.bolts.RepoWriterBolt;
import storm.spouts.TwitterSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.utils.*;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

/**
 * Topology class that defines the computational model DAG for Storm
 * @author Gabriele Proni, Gabriele de Capoa, Daniele Morgantini
 * 
 **/
public class RtwupTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		
		/*
		builder.setSpout("filteredStream", new TwitterSpout(), 1);
		builder.setBolt("expander", new ExpanderBolt(), 5).shuffleGrouping(
				"filteredStream");
		builder.setBolt("urlCounter", new URLCounterBolt(), 5).fieldsGrouping(
				"expander", new Fields("expanded_url_domain"));
		builder.setBolt("RedisPublisher", new RedisPublisherBolt(), 1).shuffleGrouping(
				"urlCounter");
		*/
		
		builder.setSpout("filteredStream", new TwitterSpout(), 1);
		builder.setBolt("expander", new ExpanderUserURLBolt(), 5).shuffleGrouping(
				"filteredStream");
		builder.setBolt("repoWriter", new RepoWriterBolt(), 5).shuffleGrouping(
				"expander");
		builder.setBolt("redisUserPublisher", new RedisUserPublisherBolt(), 5).shuffleGrouping(
				"filteredStream");
		
		Config conf = new Config();
		conf.setDebug(true);
		
		if (args != null && args.length > 0) {

			conf.setNumWorkers(3);
			conf.put("host", args[1]);
			conf.put("sw0", Double.parseDouble(args[2])); // is location always present?
			conf.put("sw1", Double.parseDouble(args[3]));
			conf.put("ne0", Double.parseDouble(args[4]));
			conf.put("ne1", Double.parseDouble(args[5]));
			int numberKeywords = 0;
			for(int i = 6; i<args.length; i++) {
				conf.put("keyword"+(i-6), args[i]);
				numberKeywords++;
			}
			conf.put("numberKeywords", numberKeywords);
			
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		} else {
			conf.put("host", "localhost");
			conf.put("sw0", 12.20);
			conf.put("sw1", 41.60);
			conf.put("ne0", 12.80);
			conf.put("ne1", 42.10);
			conf.put("keyword0", "berlusconi");
			conf.put("keyword1", "letta governo");
			conf.put("numberKeywords", 2);
			
			try{
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology("RTwUP", conf, builder.createTopology());
				Utils.sleep(400000);
				cluster.killTopology("RTwUP");
				cluster.shutdown();
			}catch (Exception e){
				System.err.println("Error");
				e.printStackTrace();
			}
		}
	}
}