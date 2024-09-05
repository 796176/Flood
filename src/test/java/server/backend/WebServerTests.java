package server.backend;

import global.RequestType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import server.bandwidth.BandwidthStatus;
import server.bandwidth.DefaultBandwidthStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class WebServerTests {
	abstract WebServer getInstance();

	@Test
	void changePort() {
		WebServer ws = getInstance();
		ws.setPort(9999);
		assertEquals(9999, ws.getPort());
	}

	@Test
	void changeBandwidthStatus() {
		WebServer ws = getInstance();
		BandwidthStatus bs = new DefaultBandwidthStatus();
		bs.setOutputDestination("/new_destination");
		ws.setBandwidthStatus(bs);
		assertEquals(bs, ws.getBandwidthStatus());
	}

	@Test
	void runCycle() {
		WebServer ws = getInstance();
		assertTimeout(Duration.of(1, ChronoUnit.SECONDS), ws::start);
		assertTimeout(Duration.of(1, ChronoUnit.SECONDS), ws::interrupt);
	}

	@Nested
	class RequestLast {
		Path output;
		WebServer ws;

		@BeforeEach
		void beforeEach() throws IOException {
			output = Files.createTempFile(null, null);
			ws = getInstance();

			BandwidthStatus bandwidthStatus = new DefaultBandwidthStatus();
			bandwidthStatus.setOutputDestination(output.toString());
			bandwidthStatus.log(1000, "data1");
			bandwidthStatus.log(1100, "data2");
			bandwidthStatus.log(1200, "data3");

			ws.setBandwidthStatus(bandwidthStatus);
			ws.setPort(54231);
		}

		@AfterEach
		void afterEach() throws IOException {
			Files.delete(output);
		}

		@Test
		void sendRegularRequest() throws IOException, InterruptedException {
			URI uri = URI.create(
				"http://localhost:54231/%s?u=%d&t=5".formatted(RequestType.retrieve_last, GregorianCalendar.SECOND)
			);
			HttpRequest request =
				HttpRequest
					.newBuilder()
					.timeout(Duration.of(1, ChronoUnit.SECONDS))
					.uri(uri)
					.build();
			ws.start();
			try (HttpClient client = HttpClient.newHttpClient()) {
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				JSONObject jsonObject = new JSONObject(response.body());
				JSONArray records = jsonObject.getJSONArray("records");
				for (Object o: records) {
					String[] record = new JSONArray(o.toString()).toList().toArray(new String[0]);
					assertTrue(record[0].matches("\\d+"), "Unix time expected");
					assertTrue(record[1].matches("1[012]00"), "Bandwidth speed expected");
					assertTrue(record[2].matches("data[123]"), "Additional info expected");
				}
			} finally {
				ws.interrupt();
			}
		}

		@Test
		void sendCorruptedRequest() throws IOException, InterruptedException {
			URI uri = URI.create(
				"http://localhost:54231/%s?u=deeznuts&t=-69".formatted(RequestType.retrieve_last)
			);
			HttpRequest request =
				HttpRequest
					.newBuilder()
					.timeout(Duration.of(1, ChronoUnit.SECONDS))
					.uri(uri)
					.build();
			ws.start();
			try (HttpClient client = HttpClient.newHttpClient()) {
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertEquals(400, response.statusCode());
			} finally {
				ws.interrupt();
			}
		}
	}

	@Nested
	class RequestRange {
		Path output;
		WebServer ws;
		long rangeStartTime;
		long rangeEndTime;

		@BeforeEach
		void beforeEach() throws IOException {
			output = Files.createTempFile(null, null);
			ws = getInstance();

			BandwidthStatus bandwidthStatus = new DefaultBandwidthStatus();
			bandwidthStatus.setOutputDestination(output.toString());
			rangeStartTime = System.currentTimeMillis();
			bandwidthStatus.log(1000, "data1");
			bandwidthStatus.log(1100, "data2");
			bandwidthStatus.log(1200, "data3");
			rangeEndTime = System.currentTimeMillis();

			ws.setBandwidthStatus(bandwidthStatus);
			ws.setPort(54231);
		}

		@AfterEach
		void afterEach() throws IOException {
			Files.delete(output);
		}

		@Test
		void sendRegularRequest() throws IOException, InterruptedException {
			URI uri = URI.create(
				"http://localhost:54231/%s?s=%d&e=%d"
					.formatted(RequestType.retrieve_range, rangeStartTime, rangeEndTime)
			);
			HttpRequest request =
				HttpRequest
					.newBuilder()
					.timeout(Duration.of(1, ChronoUnit.SECONDS))
					.uri(uri)
					.build();
			ws.start();
			try (HttpClient client = HttpClient.newHttpClient()) {
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				JSONObject jsonObject = new JSONObject(response.body());
				JSONArray records = jsonObject.getJSONArray("records");
				for (Object o: records) {
					String[] record = new JSONArray(o.toString()).toList().toArray(new String[0]);
					assertTrue(record[0].matches("\\d+"), "Unix time expected");
					assertTrue(record[1].matches("1[012]00"), "Bandwidth speed expected");
					assertTrue(record[2].matches("data[123]"), "Additional info expected");
				}
			} finally {
				ws.interrupt();
			}
		}

		@Test
		void sendCorruptedRequest() throws IOException, InterruptedException {
			URI uri = URI.create(
				"http://localhost:54231/%s?e=empty&s=:-(".formatted(RequestType.retrieve_range)
			);
			HttpRequest request =
				HttpRequest
					.newBuilder()
					.timeout(Duration.of(1, ChronoUnit.SECONDS))
					.uri(uri)
					.build();
			ws.start();
			try (HttpClient client = HttpClient.newHttpClient()) {
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertEquals(400, response.statusCode());
			} finally {
				ws.interrupt();
			}
		}
	}
}
