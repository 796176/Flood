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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
		HttpClient client = HttpClient
			.newBuilder()
			.connectTimeout(Duration.of(20, ChronoUnit.SECONDS))
			.followRedirects(HttpClient.Redirect.ALWAYS)
			.build();
		HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
		Path tmpFile = Path.of("~" + System.currentTimeMillis());
		long timeToDownload;
		try {
			long startTime = System.currentTimeMillis() / 1000;
			HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(tmpFile));
			timeToDownload = System.currentTimeMillis() / 1000 - startTime;
			if (timeToDownload == 0) timeToDownload++;
			bs.log(Files.size(tmpFile) * 8 / timeToDownload, "download_speed");
		} catch (InterruptedException | IOException exception) {
			e = exception;
		}
		finally {
			try {
				Files.delete(tmpFile);
			} catch (IOException exception) {
				e = exception;
			}
		}

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
