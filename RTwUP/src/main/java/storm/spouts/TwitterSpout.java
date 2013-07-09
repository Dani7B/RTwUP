package storm.spouts;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.auth.AccessToken;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/** 
 * 
 * This spout listens to tweet stream, then filters the tweets by location (e.g. city of Rome)
 * and retrieves only the links contained in tweets.
 * 
 * @author Gabriele de Capoa, Gabriele Proni, Daniele Morgantini
 * 
 * **/

public class TwitterSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;

	private LinkedBlockingQueue<Status> queue = null;
	private SpoutOutputCollector collector;

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {

		this.queue = new LinkedBlockingQueue<Status>();
		this.collector = collector;

		TwitterStream ts = new TwitterStreamFactory().getInstance();
		ts.setOAuthConsumer("P9c5PqNZ2HvANU6B8Rrp1A", "iKUCqCYbvI8Tam7zGgIRiO6Zcyh2hw7Nm0v97lE");
		AccessToken accessToken = new AccessToken("1546231212-TKDS2JM9sBp351uEuvnbn1VSPLR5mUKhZxwmfLr","8krjiVUEAoLvFrLC8ryw8iaU2PKTU80WHZaWevKGk2Y");
		ts.setOAuthAccessToken(accessToken);

		StatusListener listener = new StatusListener() {

			public void onException(Exception arg0) {
			}

			public void onDeletionNotice(StatusDeletionNotice arg0) {
			}

			public void onScrubGeo(long arg0, long arg1) {
			}

			public void onStallWarning(StallWarning arg0) {
			}

			public void onStatus(Status status) {
				if(status.getURLEntities().length != 0)
					queue.add(status);
			}

			public void onTrackLimitationNotice(int arg0) {
			}

		};

		ts.addListener(listener);
		FilterQuery query = new FilterQuery();
		double[][] bbox = { { 12.38, 41.80 }, { 12.60, 42.00 } };
		query.locations(bbox);
		ts.filter(query);
	}

	public void nextTuple() {
		try {
			Status retrieve = queue.take();
			URLEntity[] urls = retrieve.getURLEntities();
			for (URLEntity url : urls)
				this.collector.emit(new Values(url.getURL()));
		} catch (InterruptedException e) {
			System.err.println("ERRORE SULLO SPOUT: " + e.getMessage());
		}

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("url"));

	}

}
