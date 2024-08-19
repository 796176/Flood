package server.bandwidth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

abstract public class BandwidthStatusTests {

	abstract BandwidthStatus getInstance();

	@Test
	final void changeOutputFile() {
		BandwidthStatus bs = getInstance();
		bs.setOutputDestination("/etc/flood");
		assertEquals("/etc/flood", bs.getOutputDestination());
	}

	@Nested
	class IOOperations {
		BandwidthStatus bs = getInstance();
		Path outputFile;

		@BeforeEach
		void beforeEach() throws IOException {
			outputFile = Files.createTempFile(null, null);
			bs.setOutputDestination(outputFile.toString());
		}

		@AfterEach
		void afterEach() throws IOException{
			Files.delete(outputFile);
		}

		@Test
		void log() throws IOException{
			assertEquals(
				0,
				bs.collect(Long.MIN_VALUE, Long.MAX_VALUE).size(),
				"The status file is expected to be empty"
			);

			bs.log(86, "eighty-six");
			bs.log(796176, "yav");

			LinkedList<String[]> records = bs.collect(Long.MIN_VALUE, Long.MAX_VALUE);
			assertEquals(2, records.size(), "The amount of records doesn't match");
			assertEquals(
				1,
				records.stream().filter(r -> r[1].equals("86") && r[2].equals("eighty-six")).count(),
					"The first record is corrupted");
			assertEquals(
				1,
				records.stream().filter(r -> r[1].equals("796176") && r[2].equals("yav")).count(),
				"The second record is corrupted"
			);
		}

		@Test
		void collect() throws IOException, InterruptedException{
			assertEquals(
				0,
				bs.collect(Long.MIN_VALUE, Long.MAX_VALUE).size(),
				"The status file is expected to be empty"
			);

			long time1 = System.currentTimeMillis();
			bs.log(42, "the numbers mason");

			long time2 = System.currentTimeMillis();
			Thread.sleep(20);
			long time3 = System.currentTimeMillis();
			bs.log(9, "what do they mean?");

			assertEquals(
				2,
				bs.collect(time1).size(),
				"Retrieval of all 2 records expected"
			);
			LinkedList<String[]> firstRecord = bs.collect(time1, time2);
			assertEquals(1, firstRecord.size(), "Only the first record is expected");
			assertEquals("42", firstRecord.getFirst()[1], "The first record is expected");

			LinkedList<String[]> secondRecord = bs.collect(time3);
			assertEquals(1, secondRecord.size(), "Only the second record is expected");
			assertEquals("9", secondRecord.getFirst()[1], "The second record is expected");
		}
	}
}
