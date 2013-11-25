package storm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

/**
 * Topology class that defines the computational model DAG for Storm
 * @author Gabriele Proni, Gabriele de Capoa, Daniele Morgantini
 * 
 **/
public class RtwupTopology {

    public static final int ONE_HOUR_MSEC = 3600000;

    public static void main(String[] args) {
				
		String topologyName = null;
		String es_host = null, es_clusterName = null, es_transportPort = null;
		String redis_host = null;
		double sw0 = 0, sw1 = 0, ne0 = 0, ne1 = 0;
		int twitterSpout = 1, userExtractorBolt = 10, expanderUserURLBolt = 10,
				repoWriterBolt = 10, redisUserPublisherBolt = 10;
		List<String> keywords = new ArrayList<String>();
		
		if (args != null && args.length > 0) {

			try {
				
				Properties prop = new Properties();
	
	            //load a properties file
				prop.load(new FileInputStream(args[0]));
	
				topologyName = prop.getProperty("topologyName");
				twitterSpout = Integer.parseInt(prop.getProperty("twitterSpout"));
				userExtractorBolt = Integer.parseInt(prop.getProperty("userExtractorBolt"));
				expanderUserURLBolt = Integer.parseInt(prop.getProperty("expanderUserURLBolt"));
				repoWriterBolt = Integer.parseInt(prop.getProperty("repoWriterBolt"));
				redisUserPublisherBolt = Integer.parseInt(prop.getProperty("redisUserPublisherBolt"));
	
				es_host = prop.getProperty("es.host");
				es_clusterName = prop.getProperty("es.clusterName");
				es_transportPort = prop.getProperty("es.transportPort");
	
				redis_host = prop.getProperty("redis.host");
				
				sw0 = Double.parseDouble(prop.getProperty("location.sw0"));
				sw1 = Double.parseDouble(prop.getProperty("location.sw1"));
				ne0 = Double.parseDouble(prop.getProperty("location.ne0"));
				ne1 = Double.parseDouble(prop.getProperty("location.ne1"));
	
				keywords = Arrays.asList(prop.getProperty("keywords").split(","));
				
		 	} catch (IOException ex) {
		 		ex.printStackTrace();
		     }
		}
		
		else {
			
			topologyName = "RTwUP";
			
			es_host = "localhost";
			es_clusterName = "profileRepository";
			es_transportPort = "9300";
			
			redis_host = "localhost";

			/* Rome
			conf.put("sw0", 12.20);
			conf.put("sw1", 41.60);
			conf.put("ne0", 12.80);
			conf.put("ne1", 42.10); */
			
			/* UK & Ireland location parameters*/
			sw0 = -11.73;
			sw1 = 49.72;
			ne0 = 2.37;
			ne1 = 59.87;
			
			/* Keywords */
			String [] kw = 	{"to","the","be","of","and","a","in","that","have","I"};
			keywords = Arrays.asList(kw);
		}

        //finished parsing parameters
		
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
		
		builder.setSpout("filteredStream", new TwitterSpout(), twitterSpout);
		builder.setBolt("extractor", new UserExtractorBolt(), userExtractorBolt)
				.shuffleGrouping("filteredStream");
		builder.setBolt("expander", new ExpanderUserURLBolt(), expanderUserURLBolt)
				.shuffleGrouping("extractor");
//		builder.setBolt("repoWriter", new RepoWriterBolt(), repoWriterBolt)
//				.shuffleGrouping("expander");
//		builder.setBolt("redisUserPublisher", new RedisUserPublisherBolt(), redisUserPublisherBolt)
//				.shuffleGrouping("expander");
		
		Config conf = new Config();
		conf.setDebug(true);
		
		ObjectMapper mapper = new ObjectMapper();
			
		/* ElasticSearch Transport Client parameters */
		conf.put("es_host", es_host);
		conf.put("es_clusterName", es_clusterName);
		conf.put("es_transportPort", es_transportPort);
		
		/* Redis host */
		conf.put("redis_host", redis_host);
		
		/* Location parameters */
		conf.put("sw0", sw0);
		conf.put("sw1", sw1);
		conf.put("ne0", ne0);
		conf.put("ne1", ne1);

		try {
			String keywordsStringified = mapper.writeValueAsString(keywords);
			conf.put("keywords", keywordsStringified);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        //start local cluster
		try {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(topologyName, conf, builder.createTopology());
			if (args == null || args.length <= 0) {
				Utils.sleep(ONE_HOUR_MSEC); //wait for 1 hour
				cluster.killTopology(topologyName);
				cluster.shutdown();
			}
		} catch (Exception e) {
			System.err.println("Error");
			e.printStackTrace();
		}
		
			
	}
}