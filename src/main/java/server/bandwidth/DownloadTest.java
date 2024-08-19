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

import java.util.TimerTask;

/**
 * DownloadTest retrieves a file from a remote server, calculates the download speed, and stores that information in a status file.
 * This interface is designed to be used by {@link server.bandwidth.FloodScheduler}.
 * The speed is measured in bits per second and stored with {@link server.bandwidth.BandwidthStatus#log(long, String)}.
 * {@link HTTPDownloadTest} is an implementation that uses http protocol to download a file.
 */
public interface DownloadTest {
	/**
	 * Sets an implementation of {@link BandwidthStatus}.
	 * @param bandwidthStatus an implementation of {@link BandwidthStatus}
	 */
	void setBandwidthStatus(BandwidthStatus bandwidthStatus);

	/**
	 * Returns current implementation of {@link BandwidthStatus}.
	 * @return current implementation of {@link BandwidthStatus}
	 */
	BandwidthStatus getBandwidthStatus();

	/**
	 * Returns the exception occurred during the downloading or storing data.
	 * @return the exception occurred during the downloading or storing data
	 */
	Exception getException();

	/**
	 * Returns the runnable instance to {@link TimerTask}.
	 * @return runnable instance to {@link TimerTask}
	 */
	TimerTask toTimerTask();
}
