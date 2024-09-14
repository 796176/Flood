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

package client;

import java.io.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SettingManipulator class provides the interface to read and write Flood Client properties from and to user's setting config.
 */
public class SettingManipulator {
	/**
	 * Parameter contains all the available parameters.
	 */
	public enum Parameter {
		/**
		 * REMOTE_URL represents Flood Backend's URL.
		 */
		REMOTE_URL,
		/**
		 * MAIN_WINDOW_X represents the horizontal position of the upper-left corner of {@link client.gui.main.MainFrame}
		 * when it was closed.
		 */
		MAIN_WINDOW_X,
		/**
		 * MAIN_WINDOW_Y represents the vertical position of the upper-left corner of {@link client.gui.main.MainFrame}
		 * when it was closed.
		 */
		MAIN_WINDOW_Y,
		/**
		 * MAIN_WINDOW_HEIGHT represents the height of {@link client.gui.main.MainFrame} when it was closed.
		 */
		MAIN_WINDOW_HEIGHT,
		/**
		 * MAIN_WINDOW_WIDTH represents the width of {@link client.gui.main.MainFrame} when it was closed.
		 */
		MAIN_WINDOW_WIDTH,

		/**
		 * PROXY_PROTOCOL contains the type of proxy protocol to use to access Flood Backend.
		 * Supported protocols: SOCKS, HTTP.
		 */
		PROXY_PROTOCOL,

		/**
		 * PROXY_URL contains the address of the proxy server.
		 * It can be represented as an IP address or a domain name.
		 */
		PROXY_URL,

		/**
		 * PROXY_PORT contains the port of proxy server.
		 */
		PROXY_PORT
	}

	private static String configPath = System.getProperty("user.home") + File.separator + ".flood_client";

	/**
	 * Returns the string representation of the property according to the parameter.
	 * The available parameters are values of {@link SettingManipulator.Parameter}.<br>
	 * If the parameter was previously assigned an empty string, an empty optional will be returned.
	 * @param par parameter
	 * @return property or an empty optional if the property is absent
	 * @throws IOException if an I/O error occurs
	 */
	public static Optional<String> getValue(Parameter par) throws IOException {
		assert par != null;

		File configFile = new File(configPath);
		if (!configFile.exists()) configFile.createNewFile();

		try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(par.toString())) {
					String value = line.substring(line.indexOf("\t") + 1);
					if (!value.isEmpty())
						return Optional.of(value);
				}
			}
			return Optional.empty();
		}
	}

	/**
	 * Sets the property and the parameter.
	 * The available parameters are values of {@link SettingManipulator.Parameter}.<br>
	 * Setting an empty value is tantamount to removing it.
	 * @param par parameter
	 * @param val string representation of the property
	 * @throws IOException if an I/O error occurs
	 */
	public static void setValue(Parameter par, String val) throws IOException {
		assert par != null && val != null;

		File configFile = new File(configPath);
		if (!configFile.exists()) configFile.createNewFile();

		String configContent;
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(configFile))) {
			configContent = new String(bis.readAllBytes());
		}

		Matcher matcher = Pattern.compile(par + "\t[^\n]+").matcher(configContent);
		if (matcher.find()) {
			String oldLine = matcher.group();
			String newLine = par + "\t" + val;
			configContent = configContent.replace(oldLine, newLine);
		} else {
			configContent = configContent + System.lineSeparator() + par + "\t" + val;
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(configFile))) {
			bw.write(configContent);
		}
	}
}
