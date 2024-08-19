package server.bandwidth;

import global.SettingLoader;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DefaultBandwidthStatusTests extends BandwidthStatusTests {
	@Override
	BandwidthStatus getInstance() {
		return new DefaultBandwidthStatus();
	}

	@Nested
	class ContentManagement {
		Path outputFile;
		Path newConfigFile;
		String oldConfigFile;
		Field configField;

		@BeforeEach
		void beforeEach() throws IOException, NoSuchFieldException, IllegalAccessException {
			outputFile = Files.createTempFile(null, null);
			newConfigFile = Files.createTempFile(null, null);

			configField = SettingLoader.class.getDeclaredField("configPath");
			configField.trySetAccessible();
			oldConfigFile = (String) configField.get(null);
			configField.set(null, newConfigFile.toString());

			try (FileWriter fw = new FileWriter(newConfigFile.toString())) {
				fw.write(SettingLoader.LOG_LIMIT + " 2000");
			}
		}

		@AfterEach
		void afterEach() throws IOException, IllegalAccessException{
			Files.delete(outputFile);

			configField.set(null, oldConfigFile);
		}

		@Test
		void oldRecordsPurge() throws IOException, InterruptedException {
			DefaultBandwidthStatus bandwidthStatus = new DefaultBandwidthStatus();
			bandwidthStatus.setOutputDestination(outputFile.toString());

			bandwidthStatus.log(0xff0000, "red");
			Thread.sleep(3000);
			bandwidthStatus.log(0x00ff00, "green");

			try (BufferedReader br = new BufferedReader(new FileReader(outputFile.toString()))){
				CharBuffer buffer = CharBuffer.allocate(1024);
				br.read(buffer);
				buffer.flip();
				assertFalse(buffer.toString().contains(Integer.toString(0xff0000)));
				assertFalse(buffer.toString().contains("red"));
			}
		}
	}
}
