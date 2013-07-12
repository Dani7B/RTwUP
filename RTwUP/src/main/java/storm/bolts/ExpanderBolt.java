package storm.bolts;

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
 * This bolt expands the URL, if it is a shortned URL, until we retrieve the
 * effective URL.
 * 
 * @author Gabriele de Capoa, Gabriele Proni
 * 
 */

public class ExpanderBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	/**
	 * Expand the URL. We use code by Andrew Thompson ({@link http://
	 * stackoverflow.com/questions/10661337/expanding-a-shortened-url-to-its-original
	 * -full-length-url-in-java}).
	 */

	public void execute(Tuple input, BasicOutputCollector collector) {
		String url = input.getStringByField("url");
		URL testingUrl;
		try {
			testingUrl = new URL(url);
			URLConnection connection = testingUrl.openConnection();
			connection.getHeaderFields();
			testingUrl = connection.getURL();
			collector.emit(new Values(testingUrl.getHost(), testingUrl
					.getFile()));
		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("expanded_url_domain", "expanded_url_file"));
	}

}
