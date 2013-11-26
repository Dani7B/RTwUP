package storm.spouts;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import static storm.bolts.Fields.*;

/** 
 * This spout listens to tweet stream, then filters the tweets by location (e.g. city of Rome)
 * and retrieves only the links contained in tweets.
 * 
 * @author Gabriele de Capoa, Gabriele Proni, Daniele Morgantini
 **/

public class TwitterSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;

	private LinkedBlockingQueue<String> queue = null;
	private SpoutOutputCollector collector;
	private TwitterStream ts = null;
	private double[][] bbox = null;
	private String[] keywords;

    @Override
	public void open(Map conf, TopologyContext context,	SpoutOutputCollector collector) {

		this.bbox = new double[2][2];
		this.bbox[0][0] = (Double) conf.get("sw0");
		this.bbox[0][1] = (Double) conf.get("sw1");
		this.bbox[1][0] = (Double) conf.get("ne0");
		this.bbox[1][1] = (Double) conf.get("ne1");
		
		String keywordsStringified = (String) conf.get("keywords");
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.keywords = mapper.readValue(keywordsStringified, String[].class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.queue = new LinkedBlockingQueue<String>();
		this.collector = collector;
		final Configuration configuration = new ConfigurationBuilder().setJSONStoreEnabled(true).build();
		this.ts = new TwitterStreamFactory(configuration).getInstance();
		this.ts.setOAuthConsumer("P9c5PqNZ2HvANU6B8Rrp1A", "iKUCqCYbvI8Tam7zGgIRiO6Zcyh2hw7Nm0v97lE");
		AccessToken accessToken = new AccessToken("1546231212-TKDS2JM9sBp351uEuvnbn1VSPLR5mUKhZxwmfLr","8krjiVUEAoLvFrLC8ryw8iaU2PKTU80WHZaWevKGk2Y");
		this.ts.setOAuthAccessToken(accessToken);

		StatusListener listener = new StatusListener() {
			
			private boolean isInRange(GeoLocation gl, double[][] bbox) {
				double[] sw = bbox[0];
				double[] ne = bbox[1];
				double latitude = gl.getLatitude();
				double longitude = gl.getLongitude();
				if((latitude>=sw[1] && latitude<=ne[1])&&(longitude>=sw[0] && longitude<=ne[0]))
					return true;
				return false;
			}

			public void onException(Exception arg0) {
			}

			public void onDeletionNotice(StatusDeletionNotice arg0) {
			}

			public void onScrubGeo(long arg0, long arg1) {
			}

			public void onStallWarning(StallWarning arg0) {
			}

			@Override
			public void onStatus(Status status) {
				
				final String jsonStatus = DataObjectFactory.getRawJSON(status);
				queue.add(jsonStatus);
			}

			public void onTrackLimitationNotice(int arg0) {
			}

		};

		this.ts.addListener(listener);

        final FilterQuery query = new FilterQuery();
		query.locations(this.bbox);
		query.track(this.keywords);

        this.ts.filter(query);
	}

    @Override
	public void nextTuple() {
		try {
			final String jsonStatus = queue.take();
			this.collector.emit(new Values(jsonStatus));
		} catch (InterruptedException e) {
			System.err.println("ERRORE SULLO SPOUT: " + e.getMessage());
		}
	}

    @Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(STATUS_JSON));
	}

    @Override
	public void close(){
		this.ts.shutdown();
	} 
}