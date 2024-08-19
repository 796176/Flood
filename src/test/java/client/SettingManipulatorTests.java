package client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingManipulatorTests {
	Path newConfig;
	String oldConfig;
	Field configField;
	@BeforeEach
	void beforeEach() throws IOException, NoSuchFieldException, IllegalAccessException {
		newConfig = Files.createTempFile(null, null);

		configField = SettingManipulator.class.getDeclaredField("configPath");
		configField.trySetAccessible();
		oldConfig = (String) configField.get(null);
		configField.set(null, newConfig.toString());
	}

	@AfterEach
	void afterEach() throws IllegalAccessException, IOException {
		configField.set(null, oldConfig);
		configField.setAccessible(false);

		Files.delete(newConfig);
	}

	@Test
	void settingWriteAndReader() throws IOException {
		SettingManipulator.setValue(SettingManipulator.Parameter.REMOTE_URL, "http://example.com");
		SettingManipulator.setValue(SettingManipulator.Parameter.MAIN_WINDOW_X, "100");
		SettingManipulator.setValue(SettingManipulator.Parameter.MAIN_WINDOW_Y, "200");
		SettingManipulator.setValue(SettingManipulator.Parameter.MAIN_WINDOW_WIDTH, "300");
		SettingManipulator.setValue(SettingManipulator.Parameter.MAIN_WINDOW_HEIGHT, "400");

		assertEquals("http://example.com", SettingManipulator.getValue(SettingManipulator.Parameter.REMOTE_URL).get());
		assertEquals("100", SettingManipulator.getValue(SettingManipulator.Parameter.MAIN_WINDOW_X).get());
		assertEquals("200", SettingManipulator.getValue(SettingManipulator.Parameter.MAIN_WINDOW_Y).get());
		assertEquals("300", SettingManipulator.getValue(SettingManipulator.Parameter.MAIN_WINDOW_WIDTH).get());
		assertEquals("400", SettingManipulator.getValue(SettingManipulator.Parameter.MAIN_WINDOW_HEIGHT).get());
	}
}
