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

/**
 * RequestType enumeration contains all the options to the client to request a specific type of information.
 */
public enum RequestType {
	/**
	 * retrieve_last is used to get the available information about the network for the last several of time units.<br>
	 * "u" parameter specifies the type of time unit ( hours, minutes, etc. ) and represented via numbers. The numbers
	 * themselves are constants of {@link java.util.Calendar}.<br>
	 * "t" parameter specifies the amount of time units which must be a natural number.<br>
	 * The example of a request to retrieve all the record for the last 24 hours:<br>
	 * {@code /retrieve_last?u=10&t=24}
	 */
	retrieve_last
}
