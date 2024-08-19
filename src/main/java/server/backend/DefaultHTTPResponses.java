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

package server.backend;

public class DefaultHTTPResponses {
	public static final String _400 =
		"""
			HTTP/1.1 400 Bad Request
			Content-Length: 0
			
			""";
	public static final String _500 =
		"""
			HTTP/1.1 500 Internal Server Error
			Content-Length: 0
			
			""";
	public static final String _503 =
		"""
			HTTP/1.1 503 Service Unavailable
			Content-Length: 0
			
			""";
}
