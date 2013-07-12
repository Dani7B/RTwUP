package storm.bolts;

import java.io.IOException;
import java.net.HttpURLConnection;
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
	 * Expand the URL. We use code by Thomas Taschauer ({@link https
	 * ://gist.github.com/TomTasche/1104272} modified for our purpose.
	 * 
	 */

	public void execute(Tuple input, BasicOutputCollector collector) {
		String url = input.getStringByField("url");
		try {
			URL testingUrl;
<<<<<<< HEAD
			boolean flag = false;
			do {
				testingUrl = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) testingUrl.openConnection();
				connection.setInstanceFollowRedirects(true);
				byte[] expanded = new byte[512];
				connection.getInputStream().read(expanded, 0, 512);
				String urlExpanded = new String(expanded);
				if (urlExpanded.contains("href")) {
					urlExpanded = urlExpanded.substring(urlExpanded
							.indexOf("href") + 6);
					urlExpanded = urlExpanded.substring(0,
							urlExpanded.indexOf("\""));
					testingUrl = new URL(urlExpanded);
				} else {
					flag = true;
				}
			} while (!flag);
			collector.emit(new Values(testingUrl.getHost(), testingUrl.getFile()));
=======
			testingUrl = new URL(url);
			URLConnection connection = testingUrl.openConnection();
			connection.getHeaderFields();
			testingUrl = connection.getURL();
			collector.emit(new Values(testingUrl.getHost(), testingUrl
					.getFile()));
>>>>>>> 514e4c69113d0e36a4f3606f85d605f9f2e6e105
		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("expanded_url_domain", "expanded_url_file")); 
	}

}
