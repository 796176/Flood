package server.bandwidth;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertSame;

public class HTTPDownloadTestTests extends DownloadTestTests {
	@Override
	DownloadTest getInstance() {
		return new HTTPDownloadTest();
	}

	@Test
	void HTTPPropertySetter() {
		HTTPDownloadTest downloadTest = new HTTPDownloadTest();
		URI uri = URI.create("http://example.com/");
		downloadTest.setUri(uri);
		assertSame(uri, downloadTest.getUri());
	}
}
