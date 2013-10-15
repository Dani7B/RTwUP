package storm.bolts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.internal.logging.Logger;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * If the URL retrieved from the user is in a shortned form, this bolt expands it, until it gets the effective URL.
 * 
 * @author Gabriele de Capoa, Daniele Morgantini, Gabriele Proni
 **/

public class ExpanderUserURLBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	/**
	 * Expand the URL. We use code by Andrew Thompson ({@link http://
	 * stackoverflow.com/questions/10661337/expanding-a-shortened-url-to-its-original
	 * -full-length-url-in-java}).
	 */

	public void execute(Tuple input, BasicOutputCollector collector) {
		User user = (User) input.getValueByField("user");
		if(user.getURLEntity().getEnd()==0)
			collector.emit(new Values(user, ""));
		else {
			String url = user.getURLEntity().getExpandedURL();
			URL testingUrl;
			try {
				testingUrl = new URL(url);
				URLConnection connection = testingUrl.openConnection();
				String temp = connection.getHeaderField("Location");
				URL	newUrl = null;
				if (temp != null)
					 newUrl = new URL(temp);
				else{
					connection.getHeaderFields();
					newUrl= connection.getURL();
				}
				collector.emit(new Values(user, newUrl.toString()));
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("user", "expanded_user_url"));
	}
}