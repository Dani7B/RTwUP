package it.cybion.monitoring.rtwup.storm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import it.cybion.monitoring.rtwup.storm.bolts.ExpanderUserURLBolt;
import it.cybion.monitoring.rtwup.storm.bolts.UserExtractorBolt;
import it.cybion.monitoring.rtwup.storm.bolts.UserSnapshotWriterBolt;
import it.cybion.monitoring.rtwup.storm.spouts.TwitterSpout;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import backtype.storm.Config;
import backtype.storm.topology.TopologyBuilder;

/**
 * Topology class that defines the computational model DAG for Storm
 *
 * @author Gabriele Proni, Gabriele de Capoa, Daniele Morgantini
 */
public class RtwupTestTopology {

    private static final Logger LOGGER = LoggerFactory.getLogger(RtwupTestTopology.class);

    public static final int ONE_HOUR_MSEC = 3600000;

    private static final long ONE_MINUTE_MSEC = 60 * 60 * 1000;

    public static void main(String[] args) {

        // setup defaults
        String topologyName = null;
        String es_host = null, es_clusterName = null, es_transportPort = null;
        String redis_host = null;
        double sw0 = 0, sw1 = 0, ne0 = 0, ne1 = 0;

        //How many executors to spawn per component. see http://www.michael-noll.com/blog/2012/10/16/understanding-the-parallelism-of-a-storm-topology/
        int twitterSpoutParallHint = 1;
        int userExtractorParallHint = 10;
        int expanderUserURLBolt = 10;
        int repoWriterBoltParallHint = 10;
        int redisUserPublisherBolt = 10;

        List<String> keywords = new ArrayList<String>();
        boolean production = false;
        int numWorkers = 20;
        int maxSpoutPending = 5000;

        if (args != null && args.length > 0) {

            try {

                final Properties prop = new Properties();

                //load a properties file
                prop.load(new FileInputStream(args[0]));

                topologyName = prop.getProperty("topologyName");
                twitterSpoutParallHint = Integer.parseInt(prop.getProperty("twitterSpout"));
                userExtractorParallHint = Integer.parseInt(prop.getProperty("userExtractorBolt"));
                userExtractorParallHint = Integer.parseInt(prop.getProperty("expanderUserURLBolt"));
                //				repoWriterBolt = Integer.parseInt(prop.getProperty("repoWriterBolt"));
                //				redisUserPublisherBolt = Integer.parseInt(prop.getProperty("redisUserPublisherBolt"));
                production = Boolean.valueOf(prop.getProperty("production"));
                numWorkers = Integer.parseInt(prop.getProperty("numWorkers"));
                maxSpoutPending = Integer.parseInt(prop.getProperty("maxSpoutPending"));

                es_clusterName = prop.getProperty("it.cybion.social.insights.es.clusterName");
                es_host = prop.getProperty("it.cybion.social.insights.es.host");
                es_transportPort = prop.getProperty("it.cybion.social.insights.es.transportPort");

                redis_host = prop.getProperty("it.cybion.rtwup.redis.host");

                sw0 = Double.parseDouble(prop.getProperty("location.sw0"));
                sw1 = Double.parseDouble(prop.getProperty("location.sw1"));
                ne0 = Double.parseDouble(prop.getProperty("location.ne0"));
                ne1 = Double.parseDouble(prop.getProperty("location.ne1"));

                keywords = Arrays.asList(prop.getProperty("keywords").split(","));

            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                ex.printStackTrace();
            }
        } else {

            throw new RuntimeException("missing configuration file");

        }

        //finished parsing parameters

        final TopologyBuilder builder = new TopologyBuilder();

		/*

		builder.setSpout("filteredStream", new TwitterSpout(), 1);
		builder.setBolt("expander", new ExpanderBolt(), 5).shuffleGrouping(
				"filteredStream");
		builder.setBolt("urlCounter", new URLCounterBolt(), 5).fieldsGrouping(
				"expander", new Fields("expanded_url_domain"));
		builder.setBolt("RedisPublisher", new RedisPublisherBolt(), 1).shuffleGrouping(
				"urlCounter");

		*/

        final String filteredStreamId = "filtered-stream";
        final String userExtractorId = "user-extractor";
        final String urlExpanderId = "url-expander";
        final String repoWriterId = "repo-writer";

        //TODO setNumTasks is not configured
        builder.setSpout(filteredStreamId, new TwitterSpout(), twitterSpoutParallHint);
        builder.setBolt(userExtractorId, new UserExtractorBolt(), userExtractorParallHint)
                .shuffleGrouping(filteredStreamId);
        builder.setBolt(urlExpanderId, new ExpanderUserURLBolt(), expanderUserURLBolt)
                .shuffleGrouping(userExtractorId);
        builder.setBolt(repoWriterId, new UserSnapshotWriterBolt(), repoWriterBoltParallHint)
                .shuffleGrouping(urlExpanderId);

        //		builder.setBolt("redisUserPublisher", new RedisUserPublisherBolt(), redisUserPublisherBolt)
        //				.shuffleGrouping("expander");

        final Config topologyConfiguration = new Config();
        //		topologyConfiguration.setDebug(true);

        final ObjectMapper mapper = new ObjectMapper();

		/* ElasticSearch Transport Client parameters */
        topologyConfiguration.put("es_host", es_host);
        topologyConfiguration.put("es_clusterName", es_clusterName);
        topologyConfiguration.put("es_transportPort", es_transportPort);

		/* Redis host */
        topologyConfiguration.put("redis_host", redis_host);

		/* Location parameters */
        topologyConfiguration.put("sw0", sw0);
        topologyConfiguration.put("sw1", sw1);
        topologyConfiguration.put("ne0", ne0);
        topologyConfiguration.put("ne1", ne1);

        try {
            final String keywordsStringified = mapper.writeValueAsString(keywords);
            topologyConfiguration.put("keywords", keywordsStringified);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final StormTopologySubmitter topologySubmitter;

        if (production) {
            topologySubmitter = new ProductionTopologySubmitter(numWorkers, maxSpoutPending);
        } else {
            topologySubmitter = new TestTopologySubmitter(ONE_MINUTE_MSEC);
        }

        topologySubmitter.submit(topologyName, topologyConfiguration, builder.createTopology());
    }
}