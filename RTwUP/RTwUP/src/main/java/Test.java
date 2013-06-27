import twitter4j.Location;
import twitter4j.AccountSettings;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class Test {
	
	public static void main(String[]args) {

		try {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey("P9c5PqNZ2HvANU6B8Rrp1A")
			  .setOAuthConsumerSecret("iKUCqCYbvI8Tam7zGgIRiO6Zcyh2hw7Nm0v97lE")
			  .setOAuthAccessToken("1546231212-TKDS2JM9sBp351uEuvnbn1VSPLR5mUKhZxwmfLr")
			  .setOAuthAccessTokenSecret("8krjiVUEAoLvFrLC8ryw8iaU2PKTU80WHZaWevKGk2Y");
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();
			
            AccountSettings settings = twitter.getAccountSettings();
            System.out.println("Sleep time enabled: " + settings.isSleepTimeEnabled());
            System.out.println("Sleep end time: " + settings.getSleepEndTime());
            System.out.println("Sleep start time: " + settings.getSleepStartTime());
            System.out.println("Geo enabled: " + settings.isGeoEnabled());
            System.out.println("Listing trend locations:");
            Location[] locations = settings.getTrendLocations();
            if(locations.length == 0)
            	System.out.println("No trend locations yet");
            else {
	            for (Location location : locations) {
	                System.out.println(" " + location.getName());
	            }
            }
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get account settings: " + te.getMessage());
            System.exit(-1);
        }
		
		
	}
}