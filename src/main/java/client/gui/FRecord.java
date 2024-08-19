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

package client.gui;

/**
 * FRecord contains the recorded information about the bandwidth test performed at a specific time.
 * @param time time of the performed test in milliseconds
 * @param speed speed in bits per second
 * @param info additional information
 */
public record FRecord(long time, long speed, String info) implements Comparable<FRecord>{
	/**
	 * Compare 2 FRecord based on the {@link FRecord#time}.
	 * @param fRecord instance of FRecord to be compared with the current instance of FRecord.
	 * @return positive integer, zero, or negative integer if the current instance of FRecord is bigger, equal, or lower respectively.
	 */
	@Override
	public int compareTo(FRecord fRecord) {
		return Long.compare(time, fRecord.time());
	}
}
