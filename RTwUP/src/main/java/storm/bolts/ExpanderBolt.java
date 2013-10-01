package storm.bolts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import twitter4j.Status;
import twitter4j.URLEntity;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * If the URL retrieved from the status is in a shortned form, this bolt expands it, until it gets the effective URL.
 * 
 * @author Gabriele de Capoa, Daniele Morgantini, Gabriele Proni
 **/

public class ExpanderBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	/**
	 * Expand the URL. We use code by Andrew Thompson ({@link http://
	 * stackoverflow.com/questions/10661337/expanding-a-shortened-url-to-its-original
	 * -full-length-url-in-java}).
	 */

	public void execute(Tuple input, BasicOutputCollector collector) {
		Status status = (Status) input.getValueByField("trackedFilteredStream");
		for(URLEntity u : status.getURLEntities()){
			String url = u.getExpandedURL();
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
				collector.emit(new Values(newUrl.getHost(), newUrl.toString()));
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("expanded_url_domain", "expanded_url_complete"));
	}
}