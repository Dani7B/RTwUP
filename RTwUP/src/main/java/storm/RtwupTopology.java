package storm;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import storm.bolts.ExpanderUserURLBolt;
import storm.bolts.RedisUserPublisherBolt;
import storm.bolts.RepoWriterBolt;
import storm.bolts.UserExtractorBolt;
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
		builder.setBolt("extractor", new UserExtractorBolt(), 10).shuffleGrouping(
				"filteredStream");
		builder.setBolt("expander", new ExpanderUserURLBolt(), 10).shuffleGrouping(
				"extractor");
		builder.setBolt("repoWriter", new RepoWriterBolt(), 10).shuffleGrouping(
				"expander");
		builder.setBolt("redisUserPublisher", new RedisUserPublisherBolt(), 10).shuffleGrouping(
				"expander");
		
		Config conf = new Config();
		conf.setDebug(true);
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<String> keywords = new ArrayList<String>();

		
		if (args != null && args.length > 0) {

			conf.setNumWorkers(3);
			
			/* ElasticSearch Transport Client parameters */
			conf.put("host", args[0]);
			conf.put("clusterName", args[1]);
			conf.put("transportPort", args[2]);
			
			/* Location parameters */
			conf.put("sw0", Double.parseDouble(args[3]));// is location always present?
			conf.put("sw1", Double.parseDouble(args[4]));
			conf.put("ne0", Double.parseDouble(args[5]));
			conf.put("ne1", Double.parseDouble(args[6]));
			
			/* Keywords */
			for(int i = 7; i<args.length; i++) {
				keywords.add(args[i]);
			}

			try {
				String keywordsStringified = mapper.writeValueAsString(keywords.toArray());
				conf.put("keywords", keywordsStringified);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				StormSubmitter.submitTopology("RTwUP", conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		} else {
			conf.put("host", "localhost");
			conf.put("clusterName", "profileRepository");
			conf.put("transportPort", "9300");
			
			/* Rome
			conf.put("sw0", 12.20);
			conf.put("sw1", 41.60);
			conf.put("ne0", 12.80);
			conf.put("ne1", 42.10); */
			
			/* UK & Ireland location parameters*/
			conf.put("sw0", -11.73);
			conf.put("sw1", 49.72);
			conf.put("ne0", 2.37);
			conf.put("ne1", 59.87);
			
			/* Keywords */
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
			
			try {
				String keywordsStringified = mapper.writeValueAsString(keywords.toArray());
				conf.put("keywords", keywordsStringified);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try{
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology("RTwUP", conf, builder.createTopology());
				Utils.sleep(3600000); //wait for 1 hour
				cluster.killTopology("RTwUP");
				cluster.shutdown();
			}catch (Exception e){
				System.err.println("Error");
				e.printStackTrace();
			}
		}
	}
}