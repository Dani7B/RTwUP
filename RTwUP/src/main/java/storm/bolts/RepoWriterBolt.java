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
		User user = (User) input.getValueByField("user_expanded");
		
		TwitterUserSnapshot twitterUserSnapshot = new TwitterUserSnapshot(user, new DateTime());
		
		TwitterUserSnapshot last = null;
		try {
			last = this.repository.getLatest(twitterUserSnapshot.getUserId());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		if(last== null || this.diff.differ(twitterUserSnapshot, last)){
			try {
				this.repository.store(twitterUserSnapshot);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}