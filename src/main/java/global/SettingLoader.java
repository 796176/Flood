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

package global;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * SettingLoader is a class to extract Flood properties.
 * First, it reads the configuration in the user's home directory.
 * If the property isn't found in there, it uses the default property in {@link DefaultSettings}.
 */
public class SettingLoader {
	/**
	 * PROTOCOL contains an internet protocol to test internet bandwidth
	 */
	public final static String PROTOCOL = "PROTOCOL";
	/**
	 * HTTP_URI contains http uri of the file to download from a remote server
	 */
	public final static String HTTP_URI = "HTTP_URI";
	/**
	 * LOG_LIMIT contains a number in milliseconds to determine if the record is old enough to delete it
	 */
	public final static String LOG_LIMIT = "LOG_LIMIT";
	/**
	 * IDLE_TIME contains a number in milliseconds to wait before starting a next bandwidth test
	 */
	public final static String IDLE_TIME = "IDLE_TIME";
	/**
	 * PORT contains a port to communicate between Flood's backend and frontend
	 */
	public final static String PORT = "PORT";
	/**
	 * OUTPUT_DESTINATION contains a path to a file to store bandwidth records
	 */
	public final static String OUTPUT_DESTINATION = "OUTPUT_DESTINATION";

	/**
	 * MAX_THREADS contains a maximum number of utilized threads
	 */
	public final static String MAX_THREADS = "MAX_THREADS";

	/**
	 * PROXY_PROTOCOL contains the type of proxy protocol to use to access Flood Backend.
	 * Supported protocols: SOCKS, HTTP.
	 */
	public final static String PROXY_PROTOCOL = "PROXY_PROTOCOL";

	/**
	 * PROXY_URL contains the address of the proxy server.
	 * It can be represented as an IP address or a domain name.
	 */
	public final static String PROXY_URL = "PROXY_URL";

	/**
	 * PROXY_PORT contains the port of proxy server.
	 */
	public final static String PROXY_PORT = "PROXY_PORT";

	private static String configPath = System.getProperty("user.home") + File.separator + ".flood_settings";
	private SettingLoader() {}

	/**
	 * Parameter contains the available parameters for {@link SettingLoader#getValue(Parameter)}.
	 */
	public enum Parameter {
		PROTOCOL, HTTP_URI, LOG_LIMIT, IDLE_TIME, PORT, OUTPUT_DESTINATION, MAX_THREADS
	}

	/**
	 * Returns the property according to the parameter.
	 * The available parameters are public variables in global.SettingLoader.
	 * @param par the property parameter ( case-insensitive )
	 * @return the property if the given property exists, otherwise returns an empty {@link Optional}
	 */
	public static Optional<String> getValue(String par) {
		assert par != null;

		CharBuffer buffer = CharBuffer.allocate(8 * 1024);
		try (BufferedReader br = new BufferedReader(new FileReader(configPath))) {
		    br.read(buffer);
			buffer.flip();
		} catch (IOException ignored) {}
		String settingConfig = buffer.toString();
		Optional<String> configLine = settingConfig
			.lines()
			.filter(line -> Pattern.compile("(?i)^" + par + "[ \t]").matcher(line).find())
			.findFirst();
		if (configLine.isPresent())
			return Optional.of(configLine.get().split("[ \t]+")[1]);

		Field[] fields = DefaultSettings.class.getFields();
		Optional<Field> field =
			Arrays.stream(fields).filter(f -> f.getName().compareToIgnoreCase(par) == 0).findFirst();
		try {
			return Optional.of(field.get().get(null).toString());
		} catch (NoSuchElementException | IllegalAccessException exception) {
			return Optional.empty();
		}
	}

	/**
	 * Return the property according to the parameter.
	 * The available parameters are values of {@link SettingLoader.Parameter Parameter}.
	 * @param par the property parameter
	 * @return the property
	 */
	public static String getValue(Parameter par) {
		assert par != null;

		return getValue(par.toString()).get();
	}
}