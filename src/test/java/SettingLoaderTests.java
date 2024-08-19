import global.DefaultSettings;
import global.SettingLoader;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingLoaderTests {
	@Test
	void loadDefaultSettings() {
		assertEquals(DefaultSettings.PROTOCOL, SettingLoader.getValue(SettingLoader.PROTOCOL).get());
		assertEquals(DefaultSettings.HTTP_URI.toString(), SettingLoader.getValue(SettingLoader.HTTP_URI).get());
		assertEquals(DefaultSettings.IDLE_TIME, Long.parseLong(SettingLoader.getValue(SettingLoader.IDLE_TIME).get()));
		assertEquals(DefaultSettings.PORT, Integer.parseInt(SettingLoader.getValue(SettingLoader.PORT).get()));
		assertEquals(DefaultSettings.LOG_LIMIT, Long.parseLong(SettingLoader.getValue(SettingLoader.LOG_LIMIT).get()));
		assertEquals(DefaultSettings.OUTPUT_DESTINATION, SettingLoader.getValue(SettingLoader.OUTPUT_DESTINATION).get());
	}

	@Nested
	class LoadFromConfig {
		Path newConfigFile;
		String oldConfigFile;
		Field configField;

		@BeforeEach
		void beforeEach() throws NoSuchFieldException, IllegalAccessException, IOException {
			newConfigFile =  Files.createTempFile(null, null);

			configField = SettingLoader.class.getDeclaredField("configPath");
			configField.trySetAccessible();
			oldConfigFile = (String) configField.get(null);
			configField.set(null, newConfigFile.toString());
		}

		@AfterEach
		void afterEach() throws IOException, IllegalAccessException {
			Files.delete(newConfigFile);

			configField.set(null, oldConfigFile);
		}

		@Test
		void loadConfigSettings() throws IOException {
			try (FileWriter fw = new FileWriter(newConfigFile.toFile())){
				fw.write(SettingLoader.PROTOCOL + " " + "ftp\n");
				fw.write(SettingLoader.HTTP_URI + "\t" + "ftp://example.com/file\n");
				fw.write(SettingLoader.IDLE_TIME.toLowerCase() + " " + 86 + "\n");
				fw.write(SettingLoader.PORT.toLowerCase() + "    " + 20 + "\n");
				fw.write(SettingLoader.LOG_LIMIT + "\t\t" + 100_000 + "\n\n");
				fw.write(SettingLoader.OUTPUT_DESTINATION + "\t \t" + "/etc/flood");
			}

			assertEquals("ftp", SettingLoader.getValue(SettingLoader.PROTOCOL).get());
			assertEquals("ftp://example.com/file", SettingLoader.getValue(SettingLoader.HTTP_URI).get());
			assertEquals("86", SettingLoader.getValue(SettingLoader.IDLE_TIME).get());
			assertEquals("20", SettingLoader.getValue(SettingLoader.PORT).get());
			assertEquals(100_000, Long.parseLong(SettingLoader.getValue(SettingLoader.LOG_LIMIT).get()));
			assertEquals("/etc/flood", SettingLoader.getValue(SettingLoader.OUTPUT_DESTINATION).get());
		}
	}
}
