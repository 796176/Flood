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

/**
 * FloodScheduler interface executes a {@link DownloadTest} instance periodically in a separate thread.
 * {@link DefaultFloodScheduler} is a default implementation of FloodScheduler.
 */
public interface FloodScheduler {
	/**
	 * Starts to periodically execute {@link DownloadTest} with predetermined pauses between.
	 * @throws IllegalStateException if the FloodScheduler was already started
	 */
	void execute() throws IllegalStateException;

	/**
	 * Stops execution of {@link DownloadTest}.
	 */
	void stopExecution();

	/**
	 * Sets waiting time in milliseconds between executions of {@link DownloadTest}.
	 * @param delay time in milliseconds
	 */
	void setDelay(long delay);

	/**
	 * Returns waiting time in milliseconds between executions of {@link DownloadTest}.
	 * @return time in milliseconds
	 */
	long getDelay();

	DownloadTest getDownloadTest();

	void setDownloadTest(DownloadTest downloadTest);
}
