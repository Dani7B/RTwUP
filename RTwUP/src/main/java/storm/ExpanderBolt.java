package storm;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class ExpanderBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	public void execute(Tuple input, BasicOutputCollector collector) {
		String url = input.getStringByField("url");
		try {
			URL url_da_verificare;
			boolean flag = false;
			do {
				url_da_verificare = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) url_da_verificare
						.openConnection();
				connection.setInstanceFollowRedirects(true);
				byte[] expanded = new byte[256];
				connection.getInputStream().read(expanded, 0, 256);
				String urlExpanded = new String(expanded);
				if (urlExpanded.contains("href")) {
					urlExpanded = urlExpanded.substring(urlExpanded
							.indexOf("href") + 6);
					urlExpanded = urlExpanded.substring(0,
							urlExpanded.indexOf("\""));
					url_da_verificare = new URL(urlExpanded);
				} else {
					flag = true;
				}
			} while (!flag);
			collector.emit(new Values(url_da_verificare.getHost()));
		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("expanded_url"));
		
	}
	

}
