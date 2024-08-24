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

import java.io.*;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import static java.lang.Long.parseLong;

/**
 * DefaultBandwidthStatus is a standard implementation of {@link server.bandwidth.BandwidthStatus}, that limit the status
 * file capacity by time, which value is acquired through {@link SettingLoader#LOG_LIMIT}
 * The destination of the status files is acquired through {@link SettingLoader#OUTPUT_DESTINATION} by default.
 */
public class DefaultBandwidthStatus implements BandwidthStatus{
	private String outputDst = SettingLoader.getValue(SettingLoader.Parameter.OUTPUT_DESTINATION);

	@Override
	public void log(long speed, String additionalInfo) throws IOException {
		additionalInfo = additionalInfo.replace(System.lineSeparator(), " ");

		File outputDstFile = new File(outputDst);
		if (!outputDstFile.exists()) outputDstFile.createNewFile();

		StringBuilder buffer;
		try (
			BufferedReader br = new BufferedReader(new FileReader(outputDst))
		) {
			CharBuffer fileContent = CharBuffer.allocate((int) Files.size(Path.of(outputDst)));
			br.read(fileContent);
			fileContent.flip();

			buffer = new StringBuilder(fileContent);
			buffer.append(System.currentTimeMillis() + " " + speed + " " + additionalInfo + System.lineSeparator());
			String firstRecord = buffer.substring(0, buffer.indexOf(System.lineSeparator()));
			long firstRecordDate = parseLong(firstRecord.split(" ")[0]);
			if (
				System.currentTimeMillis() - firstRecordDate >
					parseLong(SettingLoader.getValue(SettingLoader.Parameter.LOG_LIMIT))
			) {
				buffer.delete(0, buffer.indexOf(System.lineSeparator() + System.lineSeparator().length()));
			}
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputDst))){
			bw.write(buffer.toString());
		}
	}

	@Override
	public void setOutputDestination(String destination) {
		assert destination != null;

		outputDst = destination;
	}

	@Override
	public String getOutputDestination() {
		return outputDst;
	}

	@Override
	public LinkedList<String[]> collect(long from) throws IOException {
		return collect(from, System.currentTimeMillis());
	}

	@Override
	public LinkedList<String[]> collect(long from, long to) throws IOException{
		LinkedList<String[]> records = new LinkedList<>();
		String recordString;
		try (BufferedReader br = new BufferedReader(new FileReader(new File(outputDst)))){
			while ((recordString = br.readLine()) != null && !recordString.isEmpty()) {
				long date = Long.parseLong(recordString.substring(0, recordString.indexOf(' ')));
				if (date > to) {
					return records;
				} else if (date >= from) {
					String[] record = recordString.split(" ", 3);
					records.add(record);
				}
			}
		}

		return records;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DefaultBandwidthStatus dbs) {
			return outputDst.equals(dbs.getOutputDestination());
		}
		return false;
	}
}
