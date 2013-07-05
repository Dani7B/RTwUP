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

public class ExpandingBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	public void execute(Tuple input, BasicOutputCollector collector) {
		String url = (String) input.getValue(0);		
		try{
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setInstanceFollowRedirects(true);
			byte[] expanded = new byte[256];
			connection.getInputStream().read(expanded, 0, 256);
			String urlExpanded = new String(expanded);
			urlExpanded = urlExpanded.substring(urlExpanded.indexOf("href")+6);
			urlExpanded = urlExpanded.substring(0,urlExpanded.indexOf("\""));
			URL newURL = new URL(urlExpanded);
			collector.emit(new Values(newURL.getHost()));
		} catch(MalformedURLException e){
			
		} catch (IOException e){
			
		}	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("expanded_url"));
		
	}
	

}
