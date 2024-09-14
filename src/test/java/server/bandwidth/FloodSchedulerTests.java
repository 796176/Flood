package server.bandwidth;

import global.SettingLoader;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class FloodSchedulerTests {
	abstract FloodScheduler getInstance();

	String oldConfigPath;
	Path newConfigPath;
	Field configPathField;
	Path output;

	@BeforeEach
	void beforeEach() throws IOException, IllegalAccessException, NoSuchFieldException{
		newConfigPath = Files.createTempFile(null, null);
		output = Files.createTempFile(null, null);

		configPathField = SettingLoader.class.getDeclaredField("configPath");
		configPathField.trySetAccessible();
		oldConfigPath = (String) configPathField.get(null);
		configPathField.set(null, newConfigPath.toString());

		try (FileWriter fw = new FileWriter(newConfigPath.toString())){
			fw.write(SettingLoader.OUTPUT_DESTINATION + " " + output + "\n");
			fw.write(SettingLoader.IDLE_TIME + " 1\n");
			fw.write(SettingLoader.LOG_LIMIT + " " + Long.MAX_VALUE + "\n");
			fw.write(SettingLoader.HTTP_URI + " http://localhost:54231");
		}
	}


	@ParameterizedTest
	@ValueSource(classes = HTTPDownloadTest.class)
	void executionTest(Class<DownloadTest> testClass) throws InterruptedException, InvocationTargetException,
			InstantiationException, IllegalAccessException, NoSuchMethodException {
		Constructor<DownloadTest> constructor = testClass.getDeclaredConstructor();
		constructor.trySetAccessible();
		DownloadTest test = constructor.newInstance();

		HTTP200Response webServer =
			new HTTP200Response(54231, "did i ever tell you the definition of insanity");
		webServer.start();

		FloodScheduler scheduler = getInstance();
		scheduler.setDownloadTest(test);
		scheduler.execute();
		int acceptedConnections = webServer.getAcceptedConnections();
		Thread.sleep(3000);

		scheduler.stopExecution();
		webServer.interrupt();
		webServer.join();

		assertTrue(acceptedConnections < webServer.getAcceptedConnections());
	}

	@AfterEach
	void afterEach() throws IOException, IllegalAccessException {
		Files.delete(newConfigPath);
		Files.delete(output);

		configPathField.set(null, oldConfigPath);
	}
}
