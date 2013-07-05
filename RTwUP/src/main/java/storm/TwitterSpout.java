package storm;

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
import twitter4j.conf.ConfigurationBuilder;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class TwitterSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;

	private LinkedBlockingQueue<Status> queue = null;
	private SpoutOutputCollector collector;

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {

		this.queue = new LinkedBlockingQueue<Status>();
		this.collector = collector;

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("P9c5PqNZ2HvANU6B8Rrp1A")
				.setOAuthConsumerSecret(
						"iKUCqCYbvI8Tam7zGgIRiO6Zcyh2hw7Nm0v97lE")
				.setOAuthAccessToken(
						"1546231212-TKDS2JM9sBp351uEuvnbn1VSPLR5mUKhZxwmfLr")
				.setOAuthAccessTokenSecret(
						"8krjiVUEAoLvFrLC8ryw8iaU2PKTU80WHZaWevKGk2Y");

		TwitterStream ts = new TwitterStreamFactory(cb.build()).getInstance();

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
			if (urls != null) {
				for (URLEntity url : urls)
				this.collector.emit(new Values(url.getURL()));
			}
		} catch (InterruptedException e) {
			System.err.println("ERRORE SULLO SPOUT: " + e.getMessage());
		}

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("url"));

	}

}
