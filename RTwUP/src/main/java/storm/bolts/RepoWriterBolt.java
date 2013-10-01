package storm.bolts;

import twitter4j.User;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * This bolt sends the user and the expanded user's URL to the repository.
 * 
 * @author Daniele Morgantini
 *
 **/

public class RepoWriterBolt extends BaseBasicBolt{

	private static final long serialVersionUID = 1L;

	public void execute(Tuple input, BasicOutputCollector collector) {
		User user = (User) input.getValueByField("user");
		String expandedURL = (String) input.getStringByField("expanded_user_url");
		String message = user.getName() + "(" + user.getScreenName() + ") URL: " + expandedURL;
		System.out.println(message);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}