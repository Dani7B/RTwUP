package expansion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com ), Gabriele de Capoa
 */
public class UrlExpansionTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlExpansionTestCase.class);

    @Test
    public void shouldExpand_bitly() throws IOException {

        final String shortened = "http://bit.ly/12UK40Y";
        final URL shortenedUrl = new URL(shortened);
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);

        final URLConnection connection = shortenedUrl.openConnection();
        String temp = connection.getHeaderField("Location");
		URL expandedUrl = null;
		if (temp != null){
			 expandedUrl = new URL(temp);
		}
		else{
			connection.getHeaderFields();
			expandedUrl= connection.getURL();
		}
        LOGGER.info(expandedUrl.getHost());
        LOGGER.info(expandedUrl.toString());

        assertEquals(expandedUrl, expectedUrl);
    }
    
    @Test
    public void shouldExpand_tinyurl() throws IOException {

        final String shortened = "http://tinyurl.com/opn5s25";
        final URL shortenedUrl = new URL(shortened);
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);

        final URLConnection connection = shortenedUrl.openConnection();
        String temp = connection.getHeaderField("Location");
		URL expandedUrl = null;
		if (temp != null){
			 expandedUrl = new URL(temp);
		}
		else{
			connection.getHeaderFields();
			expandedUrl= connection.getURL();
		}
        LOGGER.info(expandedUrl.getHost());
        LOGGER.info(expandedUrl.toString());

        assertEquals(expandedUrl, expectedUrl);
    }

    @Test
    public void shouldExpand_googl() throws IOException {

        final String shortened = "http://goo.gl/6jPEK";
        final URL shortenedUrl = new URL(shortened);
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);

        final URLConnection connection = shortenedUrl.openConnection();
        String temp = connection.getHeaderField("Location");
		URL expandedUrl = null;
		if (temp != null){
			 expandedUrl = new URL(temp);
		}
		else{
			connection.getHeaderFields();
			expandedUrl= connection.getURL();
		}
        LOGGER.info(expandedUrl.getHost());
        LOGGER.info(expandedUrl.toString());

        assertEquals(expandedUrl, expectedUrl);
    }

    @Test
    public void shouldNotExpand() throws IOException {

        final String shortened = "https://github.com/Dani7B/RTwUP";
        final URL shortenedUrl = new URL(shortened);
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);

        final URLConnection connection = shortenedUrl.openConnection();
        String temp = connection.getHeaderField("Location");
		URL expandedUrl = null;
		if (temp != null){
			 expandedUrl = new URL(temp);
		}
		else{
			connection.getHeaderFields();
			expandedUrl= connection.getURL();
		}
        LOGGER.info(expandedUrl.getHost());
        LOGGER.info(expandedUrl.toString());

        assertEquals(expandedUrl, expectedUrl);
    }
    
}
