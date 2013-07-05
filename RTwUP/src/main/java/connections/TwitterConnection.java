package connections;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterConnection {
	
	
	public void filteredTweet() {
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
		
		
	}
}
