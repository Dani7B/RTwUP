import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;


public class Test {
	
	public static void main(String[]args) {
			  
			TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
			twitterStream.setOAuthConsumer("P9c5PqNZ2HvANU6B8Rrp1A", "iKUCqCYbvI8Tam7zGgIRiO6Zcyh2hw7Nm0v97lE");
			AccessToken accessToken = new AccessToken("1546231212-TKDS2JM9sBp351uEuvnbn1VSPLR5mUKhZxwmfLr","8krjiVUEAoLvFrLC8ryw8iaU2PKTU80WHZaWevKGk2Y");
			twitterStream.setOAuthAccessToken(accessToken);
			
			StatusListener listener = new StatusListener() {
	            public void onStatus(Status status) {
	                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
	            }

	            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	            }

	            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	            }

	            public void onScrubGeo(long userId, long upToStatusId) {
	                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	            }

	            public void onStallWarning(StallWarning warning) {
	            	System.out.println("Got stall warning:" + warning);
	            }

	            public void onException(Exception ex) {
	                ex.printStackTrace();
	            }
	        };
	        
	        twitterStream.addListener(listener);
	        FilterQuery query = new FilterQuery();
			double[][] bbox = {{12.38,41.80}, {12.60, 42.00}};
			query.locations(bbox);
	        twitterStream.filter(query);
		
	}
    
}