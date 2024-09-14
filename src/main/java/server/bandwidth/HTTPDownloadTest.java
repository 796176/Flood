/* Flood is a network inspection tool
 * Copyright (C) 2024 Yegore Vlussove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package server.bandwidth;

import global.SettingLoader;
import server.ProxySettings;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.TimerTask;

/**
 * HTTPDownloadTest is an implementation of {@link DownloadTest} that uses http protocol.
 * The link to the remote file uses {@link URI} format.
 * The default URI is acquired through {@link SettingLoader#HTTP_URI}.
 * The default implementation of {@link BandwidthStatus} is {@link DefaultBandwidthStatus}.
 */
public class HTTPDownloadTest extends TimerTask implements DownloadTest {
	private URI uri;
	private BandwidthStatus bs = new DefaultBandwidthStatus();
	private Exception e = null;

	public HTTPDownloadTest() {
		uri = URI.create(SettingLoader.getValue(SettingLoader.Parameter.HTTP_URI));
	}

	@Override
	public void run() {
		HttpURLConnection connection = null;
		try {
			connection = buildConnection(uri.toURL());
			String query = uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery();
			String getMes = buildGetRequest(uri.getRawPath() + query);

			long startTime = System.currentTimeMillis() / 1000;
			try (OutputStream os = connection.getOutputStream()) {
				os.write(getMes.getBytes());
			}
			long timeToDownload = System.currentTimeMillis() / 1000 - startTime;

			if (connection.getResponseCode() != 200) return;
			long responseBodySize = connection.getContentLengthLong();
			if (timeToDownload == 0) timeToDownload++;
			bs.log(responseBodySize * 8 / timeToDownload, "download_speed");
		} catch (IOException exception) {
			e = exception;
		}
		finally {
			if (connection != null) connection.disconnect();
		}

	}

	private HttpURLConnection buildConnection(URL url) throws IOException {
		URLConnection urlConnection = url.openConnection(ProxySettings.getProxy());
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setConnectTimeout(60_000);
		urlConnection.connect();

		return (HttpURLConnection) urlConnection;
	}

	private String buildGetRequest(String request) {
		return """
			GET %s HTTP/1.1
			""".formatted(request);
	}

	@Override
	public void setBandwidthStatus(BandwidthStatus bandwidthStatus) {
		assert bandwidthStatus != null;
		bs = bandwidthStatus;
	}

	@Override
	public BandwidthStatus getBandwidthStatus() {
		return bs;
	}

	@Override
	public Exception getException() {
		return e;
	}

	@Override
	public TimerTask toTimerTask() {
		return this;
	}

	/**
	 * Sets URI of the remote file.
	 * @param uri URI of the remote file
	 */
	public void setUri(URI uri) {
		assert uri != null;
		this.uri = uri;
	}

	/**
	 * Returns URI of the remote file.
	 * @return URI of the remote file
	 */
	public URI getUri() {
		return uri;
	}
}
