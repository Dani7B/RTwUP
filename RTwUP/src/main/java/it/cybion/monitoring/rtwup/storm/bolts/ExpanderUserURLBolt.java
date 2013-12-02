package it.cybion.monitoring.rtwup.storm.bolts;

import it.cybion.model.twitter.User;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
	 * Expand the URL. We use code by Andrew Thompson ({http://stackoverflow.com/questions/10661337/expanding-a-shortened-url-to-its-original-full-length-url-in-java}).
	 */

    @Override
	public void execute(final Tuple input, final BasicOutputCollector collector) {
		User user = (User) input.getValueByField(it.cybion.monitoring.rtwup.storm.bolts.Fields.USER);
		String urlToEmit = null;
		String url = null;
		try {
			if(user.getUrl() == null)
				urlToEmit = "";
			else {
				url = user.getUrl(); // it is no more a t.co
				URL testingUrl = new URL(url);
				URLConnection connection = testingUrl.openConnection();
				String temp = connection.getHeaderField("Location");
				URL	newUrl = null;
				if (temp != null)
					 newUrl = new URL(temp);
				else{
					connection.getHeaderFields();
					newUrl= connection.getURL();
				}
				urlToEmit = newUrl.toString();
			}
		} catch (MalformedURLException e) {
			urlToEmit = url;
		} catch (IOException e) {
			urlToEmit = url;
		} catch (IllegalArgumentException e) {
			urlToEmit = url;
		}
		finally {
			user.setUrl(urlToEmit);
			collector.emit(new Values(user));
		}
	}

    @Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(it.cybion.monitoring.rtwup.storm.bolts.Fields.USER_EXPANDED));
	}
}