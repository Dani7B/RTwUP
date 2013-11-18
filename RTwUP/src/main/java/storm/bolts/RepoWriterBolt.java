package storm.bolts;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.joda.time.DateTime;

import de.danielbechler.diff.Configuration;
import de.danielbechler.diff.path.PropertyPath;
import it.cybion.commons.storage.diff.DiffTwitterUserSnapshot;
import it.cybion.commons.storage.repository.RepositoryException;
import it.cybion.commons.storage.repository.impls.ESTwitterUserSnapshotRepository;
import it.cybion.commons.storage.repository.twitter.TwitterUserSnapshot;
import it.cybion.model.twitter.User;
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
	
	private DiffTwitterUserSnapshot diffTwitterUserSnapshot;
	
	@Override
	public void prepare(Map conf, TopologyContext context){
		String host = (String) conf.get("es_host");
		String clusterName = (String) conf.get("es_clusterName");
		String transportPort = (String) conf.get("es_transportPort");

		// Create a TransportClient
        final Settings transportClientSettings = ImmutableSettings.settingsBuilder().put(
                "cluster.name", clusterName).build();
        Client transportClient = new TransportClient(transportClientSettings).addTransportAddress(
                new InetSocketTransportAddress(host, Integer.parseInt(transportPort)));
        
        final ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL); // to remove entities, friends, followers 

        
		this.repository = new ESTwitterUserSnapshotRepository(transportClient, mapper);
		
		//ignores timeStamp and userId fields
    	final Configuration ignoreIdAndTimeStamp = new Configuration();
    	ignoreIdAndTimeStamp.withoutProperty(PropertyPath.buildWith("timeStamp"));
    	ignoreIdAndTimeStamp.withoutProperty(PropertyPath.buildWith("userId"));
    	ignoreIdAndTimeStamp.withoutIgnoredNodes();
    	
        this.diffTwitterUserSnapshot= new DiffTwitterUserSnapshot(ignoreIdAndTimeStamp);
	}

	public void execute(Tuple input, BasicOutputCollector collector) {
		User user = (User) input.getValueByField("user_expanded");
		
		TwitterUserSnapshot twitterUserSnapshot = new TwitterUserSnapshot(user, new DateTime());
		
		TwitterUserSnapshot last = null;
		try {
			last = this.repository.getLatest(twitterUserSnapshot.getUserId());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		try {
			if(last == null){
				this.repository.store(twitterUserSnapshot);
			}
			else {
				this.diffTwitterUserSnapshot.compareInstances(twitterUserSnapshot, last);
				if(this.diffTwitterUserSnapshot.getStatus().hasChanged()) {
						this.repository.store(twitterUserSnapshot);
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}