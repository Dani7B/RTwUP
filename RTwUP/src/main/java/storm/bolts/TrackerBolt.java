package storm.bolts;

import java.util.ArrayList;
import java.util.Map;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * This bolt filters the stream according to the specified keywords
 * 
 * @author Daniele Morgantini
 **/

public class TrackerBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;
	private ArrayList<String> keywords;

	
	public void prepare(Map conf, TopologyContext context){
		String track = (String) conf.get("track");
		int comma = 0;
		int oldComma = 0;
		String element = null;
		keywords = new ArrayList<String>();
		while(comma<track.length()){
			comma = track.indexOf(",", oldComma);
			if(comma>-1) {
				element = track.substring(oldComma, comma);
				this.keywords.add(element.toLowerCase());
				oldComma = comma + 1;
			}
			else {
				element = track.substring(oldComma);
				this.keywords.add(element.toLowerCase());
				break;
			}
		}
	}

	public void execute(Tuple input, BasicOutputCollector collector) {
		boolean toSend = true;
		Status status = (Status) input.getValueByField("status");
		for(String keyword : this.keywords){
			int whiteSpace = 0;
			int oldWhiteSpace = 0;
			String element = null;
			ArrayList<String> keywordParts = new ArrayList<String>();
			while(whiteSpace<keyword.length()){
				whiteSpace = keyword.indexOf(" ", oldWhiteSpace);
				if(whiteSpace> -1) {
					element = keyword.substring(oldWhiteSpace, whiteSpace);
					keywordParts.add(element);
					oldWhiteSpace = whiteSpace + 1;
				}
				else {
					element = keyword.substring(oldWhiteSpace);
					keywordParts.add(element);
					break;
				}
			}
			
			for(String subKeyword : keywordParts)
				toSend &= keywordVerify(status, subKeyword);
			if(toSend)
				break;
		}
		if(toSend)
			collector.emit(new Values(status));
	}
	
	private boolean keywordVerify(Status status, String keyword) {
		boolean verified = false;
		boolean text = false;
		boolean link = false;
		boolean media = false;
		boolean hashtag = false;
		boolean userMention = false;
		if(status.getText().toLowerCase().contains(keyword))
			text |= true;
		for(URLEntity url : status.getURLEntities()) {
			if(url.getExpandedURL().toLowerCase().contains(keyword)||url.getDisplayURL().toLowerCase().contains(keyword))
				link |= true;
		}
		for(MediaEntity m : status.getMediaEntities()) {
			if(m.getExpandedURL().toLowerCase().contains(keyword)||m.getDisplayURL().toLowerCase().contains(keyword))
				media |= true;
		}
		for(HashtagEntity h : status.getHashtagEntities()) {
			if(h.getText().toLowerCase().contains(keyword))
				hashtag |= true;
		}
		for(UserMentionEntity user : status.getUserMentionEntities()) {
			if(user.getScreenName().toLowerCase().contains(keyword))
				userMention |= true;
		}
		if(text||link||media||hashtag||userMention)
			verified = true;
		return verified;
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("trackedFilteredStream"));
	}

}