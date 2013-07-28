import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

/**
 * This class tests uses Twitter Stream API to connect to the Twitter service and prints on screen only 
 * the statuses with links tweeted from Rome urban area. 
 * 
 * @author Daniele Morgantini
 * 
 * **/
public class Test {
	
	private static double[][] bbox = {{12.20, 41.60},{12.80, 42.10}};
	
	public static void main(String[]args) {
			  
			TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
			twitterStream.setOAuthConsumer("P9c5PqNZ2HvANU6B8Rrp1A", "iKUCqCYbvI8Tam7zGgIRiO6Zcyh2hw7Nm0v97lE");
			AccessToken accessToken = new AccessToken("1546231212-TKDS2JM9sBp351uEuvnbn1VSPLR5mUKhZxwmfLr","8krjiVUEAoLvFrLC8ryw8iaU2PKTU80WHZaWevKGk2Y");
			twitterStream.setOAuthAccessToken(accessToken);
			
			
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
				
	            public void onStatus(Status status) {
	            	if(status.getURLEntities().length != 0) {
	            		GeoLocation gl = status.getGeoLocation();
	            		if(gl == null || isInRange(gl,bbox))
	            			System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
	            	}
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
			query.locations(bbox);
	        twitterStream.filter(query);
		
	}
}