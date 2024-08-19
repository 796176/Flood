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
		assertNotEquals(null, getInstance().toTimerTask());
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
		void download() {
			HTTP200Response webServer = new HTTP200Response(54231, "i have no idea what to write");
			webServer.start();

			DownloadTest downloadTest = getInstance();
			downloadTest.toTimerTask().run();
			assertEquals(1, webServer.getAcceptedConnections());
			assertNull(downloadTest.getException());

			webServer.interrupt();
		}
	}
}
