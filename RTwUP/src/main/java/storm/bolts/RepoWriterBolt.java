package storm.bolts;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.joda.time.DateTime;

import it.cybion.commons.storage.diff.DiffTwitterUserSnapshot;
import it.cybion.commons.storage.repository.RepositoryException;
import it.cybion.commons.storage.repository.impls.ESTwitterUserSnapshotRepository;
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
	
	private DiffTwitterUserSnapshot diff;
	
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
		
		this.diff = new DiffTwitterUserSnapshot();
	}

	public void execute(Tuple input, BasicOutputCollector collector) {
		User user = (User) input.getValueByField("user");
		String expandedURL = (String) input.getStringByField("expanded_user_url");
		
		TwitterUserSnapshot twitterUserSnapshot = createTwitterUserSnapshot(user, expandedURL);
		
		TwitterUserSnapshot last = null;
		try {
			last = this.repository.getLatest(twitterUserSnapshot.getUserId());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		if(this.diff.differ(twitterUserSnapshot, last)){
			try {
				this.repository.store(twitterUserSnapshot);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
	
	
	private static TwitterUserSnapshot createTwitterUserSnapshot(User user, String expanded_url) {
		it.cybion.model.twitter.User twitterUser = new it.cybion.model.twitter.User(user.getId(), user.getScreenName());
		twitterUser.setCreatedAt(user.getCreatedAt());
		twitterUser.setFavouritesCount(user.getFavouritesCount());
		twitterUser.setDescription(user.getDescription());
		twitterUser.setFollowersCount(user.getFollowersCount());
		twitterUser.setFriendsCount(user.getFriendsCount());
		twitterUser.setContributorsEnabled(user.isContributorsEnabled());
		twitterUser.setGeoEnabled(user.isGeoEnabled());
		twitterUser.setProtected(user.isProtected());
		twitterUser.setLang(user.getLang());
		twitterUser.setListedCount(user.getListedCount());
		twitterUser.setLocation(user.getLocation());
		twitterUser.setName(user.getName());
		twitterUser.setStatusesCount(user.getStatusesCount());
		twitterUser.setTimeZone(user.getTimeZone());
		twitterUser.setProfileImageUrl(user.getProfileImageURL());
		twitterUser.setUrl(user.getURL());
		twitterUser.setUtcOffset(user.getUtcOffset());
		twitterUser.setVerified(user.isVerified());
				
		/* These remain from the constructor of it.cybion.model.twitter.User:
		    boolean defaultProfileImage, Entities entities, List<User> followers,
            List<User> friends, String token, String tokenSecret,Tweet status.
        */
		
		return new TwitterUserSnapshot(twitterUser, new DateTime());
	}

}