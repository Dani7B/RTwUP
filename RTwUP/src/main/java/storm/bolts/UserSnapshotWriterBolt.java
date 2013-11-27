package storm.bolts;

import java.util.Map;

import it.cybion.commons.storage.repository.configuration.Indices;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static storm.bolts.Fields.*;

/**
 * This bolt stores the user snapshot into an ElasticSearch repository.
 * 
 * @author Daniele Morgantini
 *
 **/

public class UserSnapshotWriterBolt extends BaseBasicBolt{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSnapshotWriterBolt.class);

    private static final long serialVersionUID = 1L;

    private ESTwitterUserSnapshotRepository repository;
	
	private DiffTwitterUserSnapshot diffTwitterUserSnapshot;

    private Configuration ignoreIdAndTimeStamp;

    @Override
	public void prepare(Map conf, TopologyContext context){

        final String host = (String) conf.get("es_host");
        final String clusterName = (String) conf.get("es_clusterName");
        final String transportPort = (String) conf.get("es_transportPort");

		// Create a TransportClient
        final Settings transportClientSettings = ImmutableSettings.settingsBuilder().put(
                "cluster.name", clusterName).build();

        final Client transportClient = new TransportClient(transportClientSettings).addTransportAddress(
                new InetSocketTransportAddress(host, Integer.parseInt(transportPort)));
        
        final ObjectMapper mapper = new ObjectMapper();
        // to remove entities, friends, followers
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        this.repository = new ESTwitterUserSnapshotRepository(Indices.Aliases.USER_SNAPSHOTS,
                                                              transportClient, mapper);
		
		//ignores timeStamp and userId fields
    	this.ignoreIdAndTimeStamp = new Configuration();
        this.ignoreIdAndTimeStamp.withoutProperty(PropertyPath.buildWith("timeStamp"));
        this.ignoreIdAndTimeStamp.withoutProperty(PropertyPath.buildWith("userId"));
        this.ignoreIdAndTimeStamp.withoutIgnoredNodes();
	}

    @Override
	public void execute(Tuple input, BasicOutputCollector collector) {

        this.diffTwitterUserSnapshot = new DiffTwitterUserSnapshot(ignoreIdAndTimeStamp);

		final User user = (User) input.getValueByField(USER_EXPANDED);

        final DateTime now = new DateTime();

        final TwitterUserSnapshot current = new TwitterUserSnapshot(user, now);

        TwitterUserSnapshot last = null;

        try {
            last = this.repository.getLatest(current.getUserId());
        } catch (RepositoryException e) {
            System.out.println("error: '" + e.getMessage() + "'");
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        try {
            if (last == null) {
                this.repository.store(current);
            } else {
                this.diffTwitterUserSnapshot.compareInstances(current, last);
                if (this.diffTwitterUserSnapshot.getStatus().hasChanged()) {
                    this.repository.store(current);
                }
            }
        } catch (RepositoryException e) {
            System.out.println("error: '" + e.getMessage() + "'");
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
//        LOGGER.info("wrote profile of user '" + user.toString() + "'");
    }

    @Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

    @Override
    public void cleanup(){
        //nop
    }

}