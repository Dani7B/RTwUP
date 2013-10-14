package storm;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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
		
		ObjectMapper mapper = new ObjectMapper();
		
		if (args != null && args.length > 0) {

			conf.setNumWorkers(3);
			conf.put("host", args[1]);
			conf.put("sw0", Double.parseDouble(args[2])); // is location always present?
			conf.put("sw1", Double.parseDouble(args[3]));
			conf.put("ne0", Double.parseDouble(args[4]));
			conf.put("ne1", Double.parseDouble(args[5]));
			ArrayList<String> keywords = new ArrayList<String>();
			for(int i = 6; i<args.length; i++) {
				keywords.add(args[i]);
			}
			String keywordsStringified = "";
			try {
				keywordsStringified = mapper.writeValueAsString(keywords.toArray());
			} catch (JsonGenerationException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			conf.put("keywords", keywordsStringified);
			
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		} else {
			conf.put("host", "localhost");
			/* Rome
			conf.put("sw0", 12.20);
			conf.put("sw1", 41.60);
			conf.put("ne0", 12.80);
			conf.put("ne1", 42.10); */
			
			/* UK & Ireland */
			conf.put("sw0", -11.73);
			conf.put("sw1", 49.72);
			conf.put("ne0", 2.37);
			conf.put("ne1", 59.87);
			
			ArrayList<String> keywords = new ArrayList<String>();
			keywords.add("to");
			keywords.add("the");
			keywords.add("be");
			keywords.add("of");
			keywords.add("and");
			keywords.add("a");
			keywords.add("in");
			keywords.add("that");
			keywords.add("have");
			keywords.add("I");
			
			String keywordsStringified = "";
			try {
				keywordsStringified = mapper.writeValueAsString(keywords.toArray());
			} catch (JsonGenerationException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			conf.put("keywords", keywordsStringified);
			
			try{
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology("RTwUP", conf, builder.createTopology());
				Utils.sleep(4000000); //400000
				cluster.killTopology("RTwUP");
				cluster.shutdown();
			}catch (Exception e){
				System.err.println("Error");
				e.printStackTrace();
			}
		}
	}
}