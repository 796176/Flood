package server.bandwidth;

import global.SettingLoader;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

abstract public class DownloadTestTests {
	abstract DownloadTest getInstance();

	@Test
	void timerTaskCast() {
		assertNotNull(getInstance().toTimerTask());
	}

	@Test
	void propertySetter() {
		DownloadTest downloadTest = getInstance();
		BandwidthStatus bandwidthStatus = new DefaultBandwidthStatus();
		downloadTest.setBandwidthStatus(bandwidthStatus);
		assertSame(bandwidthStatus, downloadTest.getBandwidthStatus());
	}

	@Nested
	class DownloadingTest {
		Path newConfigFile;
		Path output;
		String oldConfigFile;
		Field configField;

		@BeforeEach
		void beforeEach() throws IOException, NoSuchFieldException, IllegalAccessException {
			newConfigFile = Files.createTempFile(null, null);
			output = Files.createTempFile(null, null);

			configField = SettingLoader.class.getDeclaredField("configPath");
			configField.trySetAccessible();
			oldConfigFile = (String) configField.get(null);
			configField.set(null, newConfigFile.toString());

			try (FileWriter fw = new FileWriter(newConfigFile.toString())) {
			    fw.write(SettingLoader.HTTP_URI + " http://localhost:54231\n");
				fw.write(SettingLoader.OUTPUT_DESTINATION + " " + output);
			}
		}

		@AfterEach
		void afterEach() throws IOException, IllegalAccessException {
			Files.delete(newConfigFile);
			Files.delete(output);

			configField.set(null, oldConfigFile);
		}

		@Test
		void download() throws InterruptedException {
			HTTP200Response webServer = new HTTP200Response(54231, "i have no idea what to write");
			webServer.start();

			DownloadTest downloadTest = getInstance();
			downloadTest.toTimerTask().run();
			webServer.interrupt();
			webServer.join();

			assertEquals(1, webServer.getAcceptedConnections(), "The server hasn't been reached");
			assertNull(downloadTest.getException(), () -> {
				downloadTest.getException().printStackTrace();
				return "Receiving or recording has failed";
			});
		}

		@Nested
		class WithProxyEnabled {
			StringBuilder currentConfig = new StringBuilder();
			int proxyPort = 12345;

			@BeforeEach
			void beforeEach() throws IOException {
				try (FileReader fr = new FileReader(newConfigFile.toString())) {
					int c;
					while ((c = fr.read()) != -1) {
						currentConfig.append(Character.toString(c));
					}
				}

				try (FileWriter fw = new FileWriter(newConfigFile.toString(), true)) {
					fw.write("\n");
					fw.write(SettingLoader.PROXY_PROTOCOL + " HTTP\n");
					fw.write(SettingLoader.PROXY_URL + " localhost\n");
					fw.write(SettingLoader.PROXY_PORT + " " + proxyPort);
				}
			}

			@AfterEach
			void afterEach() throws IOException {
				try (FileWriter fw = new FileWriter(newConfigFile.toString())){
					fw.write(currentConfig.toString());
				}
			}

			@Test
			void download() throws InterruptedException {
				HTTP200Response webServer = new HTTP200Response(54231, "my body is ready");
				webServer.start();
				HttpProxy httpProxy = new HttpProxy(proxyPort);
				httpProxy.start();

				DownloadTest downloadTest = getInstance();
				downloadTest.toTimerTask().run();

				httpProxy.interrupt();
				httpProxy.join();
				webServer.interrupt();
				webServer.join();

				assertNull(downloadTest.getException(), () -> {
					downloadTest.getException().printStackTrace();
					return "Receiving or recording has failed";
				});
				assertEquals(1, httpProxy.getAcceptedConnections(), "The proxy hasn't been reached");
				assertEquals(1, webServer.getAcceptedConnections(), "The web server hasn't been reached");
			}
		}
	}
}
