package storm.bolts;

import it.cybion.model.twitter.User;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import twitter4j.internal.logging.Logger;
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
 * 
 * @author Daniele Morgantini
 **/

public class UserExtractorBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	private ObjectMapper mapper;
		
	@Override
	public void prepare(Map conf, TopologyContext context){
		
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		this.mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		this.mapper.setPropertyNamingStrategy(
				PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
	    
		// Mon Mar 05 22:08:25 +0000 2007
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
		this.mapper.setDateFormat(format);
	}
	
	public void execute(Tuple input, BasicOutputCollector collector) {
		final String statusJSON = (String) input.getValueByField("statusJSON");
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

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("user"));
	}
}