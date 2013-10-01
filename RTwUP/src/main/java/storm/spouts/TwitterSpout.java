package storm.spouts;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/** 
 * This spout listens to tweet stream, then filters the tweets by location (e.g. city of Rome)
 * and retrieves only the links contained in tweets.
 * 
 * @author Gabriele de Capoa, Gabriele Proni, Daniele Morgantini
 **/

public class TwitterSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;

	private LinkedBlockingQueue<Status> queue = null;
	private SpoutOutputCollector collector;
	private TwitterStream ts = null;
	private double[][] bbox = null;
	private String[] keywords;

	public void open(Map conf, TopologyContext context,	SpoutOutputCollector collector) {

		this.bbox = new double[2][2];
		this.bbox[0][0] = (Double) conf.get("sw0");
		this.bbox[0][1] = (Double) conf.get("sw1");
		this.bbox[1][0] = (Double) conf.get("ne0");
		this.bbox[1][1] = (Double) conf.get("ne1");
		long numberKeywords = (Long) conf.get("numberKeywords");
		this.keywords = new String[(int) numberKeywords];
		for(int i=0; i<numberKeywords; i++)
			this.keywords[i] = (String) conf.get("keyword"+i);
		
		this.queue = new LinkedBlockingQueue<Status>();
		this.collector = collector;
		this.ts = new TwitterStreamFactory().getInstance();
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

			public void onStatus(Status status) {
				if(status.getURLEntities().length != 0) {
            		GeoLocation gl = status.getGeoLocation();
            		if(gl == null || isInRange(gl,bbox))
            			queue.add(status);
				}
			}

			public void onTrackLimitationNotice(int arg0) {
			}

		};

		this.ts.addListener(listener);
		FilterQuery query = new FilterQuery();
		query.locations(this.bbox);
		query.track(this.keywords);
		this.ts.filter(query);
	}

	public void nextTuple() {
		try {
			Status retrieve = queue.take();
			this.collector.emit(new Values(retrieve.getUser()));
		} catch (InterruptedException e) {
			System.err.println("ERRORE SULLO SPOUT: " + e.getMessage());
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("user"));
	}

	public void close(){
		this.ts.shutdown();
	} 
}