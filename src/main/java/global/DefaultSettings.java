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

import java.io.File;
import java.net.URI;

/**
 * DefaultSettings contains default parameters for Flood
 */
public class DefaultSettings {
	public final static String PROTOCOL = "http";
	public final static URI HTTP_URI = URI.create("https://download.mozilla.org/?product=firefox-latest-ssl&os=win64&lang=en-US");
	public final static long LOG_LIMIT = 30L * 24 * 60 * 60 * 1000;	// 30 days
	public final static long IDLE_TIME = 60L * 60 * 1000; // 1 hour
	public final static int PORT = 53333;
	public final static String OUTPUT_DESTINATION = System.getProperty("user.home") + File.separator + "flood";
	public final static int MAX_THREADS = 8;
	public final static String PROXY_PROTOCOL = null;
	public final static String PROXY_URL = null;
	public final static String PROXY_PORT = null;
}
