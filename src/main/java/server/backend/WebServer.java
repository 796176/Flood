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

import server.bandwidth.BandwidthStatus;

/**
 * WebServer interface provides the ability for remote machines to retrieve the data associated with {@link BandwidthStatus}.
 * WebServer uses HTTP protocol for communication, where the client side sends the GET request containing {@link global.RequestType}
 * and the necessary parameters.
 * The server side sends the response where the data is placed in the body part. The data itself is a json object, which contains
 * the array of records with attribute "records" where the record is 3-length string array where the first string is
 * the data of the record in milliseconds since Jan. 1st 1970, the second string is measured bandwidth speed in bits per second,
 * and the third string, which can be empty, is additional information.
 * {@link FloodWebServer} is a default implementation of WebServer.
 */
public interface WebServer {
	/**
	 * Starts the server.
	 */
	void start();

	/**
	 * Stops the server.
	 */
	void interrupt();

	/**
	 * Sets the listening port of the server.
	 * @param port listening port of the server
	 */
	void setPort(int port);

	/**
	 * Returns the listening port of the server.
	 * @return listening port of the server
	 */
	int getPort();

	/**
	 * Sets the instance of {@link BandwidthStatus}.
	 * @param bandwidthStatus instance of {@link BandwidthStatus}
	 */
	void setBandwidthStatus(BandwidthStatus bandwidthStatus);

	/**
	 * Returns the instance of {@link BandwidthStatus}.
	 * @return instance of {@link BandwidthStatus}
	 */
	BandwidthStatus getBandwidthStatus();
}
