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

import java.io.IOException;
import java.util.LinkedList;

/**
 * BandwidthStatus is an interface to keep a record of the network speed.
 * {@link server.bandwidth.BandwidthStatus} writes its test results using {@link server.bandwidth.BandwidthStatus#log(long, String)}.
 * {@link server.bandwidth.DefaultBandwidthStatus} is a barebone implementation of BandwidthStatus.
 */
public interface BandwidthStatus {
	/**
	 * Saves the information about the network speed
	 * @param speed speed in bits per second
	 * @param additionalInfo appendable information
	 * @throws IOException if an I/O error occurs
	 */
	void log(long speed, String additionalInfo) throws IOException;

	/**
	 * Sets destination of the status file
	 * @param destination destination to the file
	 */
	void setOutputDestination(String destination);

	/**
	 * Returns  destination of the status file
	 * @return  destination of the file
	 */
	String getOutputDestination();

	/**
	 * Invocation of this method is analogous to the invocation of collect(from, System.currentTimeMillis().
	 * @param from inclusive absolute time of the beginning of the period in milliseconds since Jan. 1st 1970
	 * @return linked list of records
	 * @throws IOException if an I/O error occurred
	 */
	LinkedList<String[]> collect(long from) throws IOException;

	/**
	 * Returns a linked list of records within a specified time period where the record is 3-length string array where
	 * the first string is the data of the record in milliseconds since Jan. 1st 1970, the second string is
	 * measured bandwidth speed in bits per second, and the third string, which may be empty, is additional information.
	 * @param from inclusive absolute time of the beginning of the period in milliseconds since Jan. 1st 1970
	 * @param to inclusive absolute time of the end of the period in milliseconds since Jan. 1st 1970
	 * @return linked list of records
	 * @throws IOException if an I/O error occurred
	 */
	LinkedList<String[]> collect(long from, long to) throws IOException;
}
