package ranking;

//import org.apache.log4j.PropertyConfigurator;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import storage.PageDictionary;
import static org.testng.Assert.*;

/**
 * 
 * @author Gabriele de Capoa, Gabriele Proni
 * 
 */

public class PageDictionaryTestCase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PageDictionaryTestCase.class);

	private PageDictionary counts;

	@BeforeClass
	public void setUp() {
		this.counts = PageDictionary.getInstance();
		//PropertyConfigurator.configure("src/test/resources/log4j.properties");
	}

	@Test
	public void shouldAddToDictionary() {
		int count1 = this.counts.addToDictionary("youtube.com",
				"http://youtube.com/123");
		LOGGER.info("Counts: " + count1);
		assertEquals(count1, 1);
		int count2 = this.counts.addToDictionary("youtube.com",
				"http://youtube.com/123");
		LOGGER.info("Counts: " + count2);
		assertEquals(count2, 2);
		this.counts.removeToDictionary("youtube.com", "http://youtube.com/123");
	}

	@Test
	public void shouldStringifiedRanking() throws JSONException {
		this.counts.addToDictionary("youtube.com", "http://youtube.com/123");
		this.counts.addToDictionary("youtube.com", "http://youtube.com/123");
		this.counts.addToDictionary("youtube.com", "http://youtube.com/123");
		this.counts.addToDictionary("youtube.com", "http://youtube.com/456");
		this.counts.addToDictionary("youtube.com", "http://youtube.com/456");
		this.counts
				.addToDictionary("instagram.com", "http://instagram.com/abc");
		this.counts.addToDictionary("foursquare.com",
				"http://foursquare.com/a1b");

		String actual = this.counts.getTopNelementsStringified(10);

		JSONObject json = new JSONObject();
		JSONObject frequency1 = new JSONObject();
		frequency1.put("page", "http://youtube.com/123");
		frequency1.put("count", "3 times");
		json.accumulate("youtube.com", frequency1);
		JSONObject frequency2 = new JSONObject();
		frequency2.put("page", "http://youtube.com/456");
		frequency2.put("count", "2 times");
		json.accumulate("youtube.com", frequency2);
		JSONObject frequency3 = new JSONObject();
		frequency3.put("page", "http://instagram.com/abc");
		frequency3.put("count", "1 times");
		json.accumulate("instagram.com", frequency3);
		JSONObject frequency4 = new JSONObject();
		frequency4.put("page", "http://foursquare.com/a1b");
		frequency4.put("count", "1 times");
		json.accumulate("foursquare.com", frequency4);

		String expected = json.toString();
		LOGGER.info(expected);
		LOGGER.info(actual);

		assertEquals(actual, expected);
	}

}
