package ranking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import storage.PageDictionary;
import static org.testng.Assert.*;

/**
 * 
 * @author Gabriele de Capoa, Gabriele Proni, Daniele Morgantini
 * 
 */

public class PageDictionaryTestCase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PageDictionaryTestCase.class);

	private PageDictionary counts;

	@BeforeClass
	public void setUp() {
		this.counts = PageDictionary.getInstance();
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
		this.counts.removeFromDictionary("youtube.com", "http://youtube.com/123");
	}

	@Test
	public void shouldStringifiedRanking() {
		this.counts.addToDictionary("youtube.com", "http://youtube.com/123");
		this.counts.addToDictionary("youtube.com", "http://youtube.com/123");
		this.counts.addToDictionary("youtube.com", "http://youtube.com/123");
		this.counts.addToDictionary("youtube.com", "http://youtube.com/456");
		this.counts.addToDictionary("youtube.com", "http://youtube.com/456");
		this.counts.addToDictionary("instagram.com", "http://instagram.com/abc");
		this.counts.addToDictionary("foursquare.com", "http://foursquare.com/a1b");

		String actual = this.counts.getTopNelementsStringified(10);
		
		String expected = "{\"instagram.com\":{\"pageCountList\":[{\"page\":\"http://instagram.com/abc\",\"count\":\"1 times\"}]},"
				+ "\"foursquare.com\":{\"pageCountList\":[{\"page\":\"http://foursquare.com/a1b\",\"count\":\"1 times\"}]},"
				+ "\"youtube.com\":{\"pageCountList\":[{\"page\":\"http://youtube.com/123\",\"count\":\"3 times\"},"
				+ "{\"page\":\"http://youtube.com/456\",\"count\":\"2 times\"}]}}";

		LOGGER.info(expected);
		LOGGER.info(actual);

		assertEquals(actual, expected);
	}

}
