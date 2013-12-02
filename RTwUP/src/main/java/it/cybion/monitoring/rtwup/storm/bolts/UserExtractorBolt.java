package it.cybion.monitoring.rtwup.storm.bolts;

import it.cybion.model.twitter.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;


/**
 * Simple bolt to extract the user from the raw json of the Tweet
 * 
 * @author Daniele Morgantini
 **/

public class UserExtractorBolt extends BaseBasicBolt {

    private static final long serialVersionUID = -6430208441868200779L;

    private ObjectMapper mapper;
		
	@Override
	public void prepare(final Map conf, final TopologyContext context){
		
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		this.mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		this.mapper.setPropertyNamingStrategy(
				PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
	    
		// Mon Mar 05 22:08:25 +0000 2007
		this.mapper.setDateFormat(new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy"));
	}

    @Override
	public void execute(final Tuple input, final BasicOutputCollector collector) {
		final String statusJSON = (String) input.getValueByField(it.cybion.monitoring.rtwup.storm.bolts.Fields.STATUS_JSON);
		User user = null;
		try {
			final JSONObject statusJSONObject = new JSONObject(statusJSON);
			final String userObject = (String) statusJSONObject.getString("user");
			user = this.mapper.readValue(userObject, User.class);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		collector.emit(new Values(user));
	}

    @Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(it.cybion.monitoring.rtwup.storm.bolts.Fields.USER));
	}
}