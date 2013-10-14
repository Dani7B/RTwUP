package storm.bolts;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.joda.time.DateTime;

import it.cybion.commons.storage.repository.impls.ESTwitterUserSnapshotRepository;
import it.cybion.commons.storage.repository.twitter.TwitterUser;
import it.cybion.commons.storage.repository.twitter.TwitterUserSnapshot;
import twitter4j.User;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * This bolt stores the user snapshot into an ElasticSearch repository.
 * 
 * @author Daniele Morgantini
 *
 **/

public class RepoWriterBolt extends BaseBasicBolt{

	private static final long serialVersionUID = 1L;
	
	private ESTwitterUserSnapshotRepository repository;
	
	/*
	@Override
	public void prepare(Map conf, TopologyContext context){
		String host = (String) conf.get("host");
		String clusterName = (String) conf.get("clusterName");
		String transportPort = (String) conf.get("transportPort");

		// Create a TransportClient
        final Settings transportClientSettings = ImmutableSettings.settingsBuilder().put(
                "cluster.name", clusterName).build();
        Client transportClient = new TransportClient(transportClientSettings).addTransportAddress(
                new InetSocketTransportAddress(host, Integer.parseInt(transportPort)));
        
        ObjectMapper mapper = new ObjectMapper();
        
		this.repository = new ESTwitterUserSnapshotRepository(transportClient, mapper);
	}*/

	public void execute(Tuple input, BasicOutputCollector collector) {
		User user = (User) input.getValueByField("user");
		String expandedURL = (String) input.getStringByField("expanded_user_url");
		TwitterUserSnapshot twitterUserSnapshot = createTwitterUserSnapshot(user, expandedURL);
		
		String message = user.getName() + "(" + user.getScreenName() + ") URL: " + expandedURL;
		System.out.println(message);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
	
	
	private static TwitterUserSnapshot createTwitterUserSnapshot(User user, String expanded_url) {
		TwitterUser tu = new TwitterUser(Long.toString(user.getId()), user.getName(), user.getScreenName(),
							user.getDescription(), expanded_url, user.getFollowersCount(), 
							user.getFriendsCount(), user.getStatusesCount(), user.isVerified());
		return new TwitterUserSnapshot(tu, new DateTime());
	}

}