package expansion;

//import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TestingURL {

	public TestingURL() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws IOException {
		final String shortened = "http://bit.ly/12UK40Y";
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);

        URL expandedUrl;
		URL testingUrl;
		try {
			testingUrl = new URL(shortened);
			URLConnection connection = testingUrl.openConnection();
			connection.getHeaderFields();
			expandedUrl = connection.getURL();
			System.out.println(expandedUrl.toString() + " - " +expectedUrl.toString());
		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
	}

}
