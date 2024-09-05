/*
 * Flood is a network inspection tool
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
 *
 */

package client.gui.update;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * SpinnerTimeModel is a model for {@link JSpinner} to display and edit time of 24-hour format.
 * Time is presented as a string and has hh:mm pattern.
 * For JSpinner to support SpinnerTimeModel it has to have {@link TimeEditor} as its editor.
 */
public class SpinnerTimeModel extends AbstractSpinnerModel {
	private int hours;
	private int minutes;

	/**
	 * Returns a string representation of time with hh:mm pattern
	 * @return string representation of time
	 */
	@Override
	public Object getValue() {
		return "%02d:%02d".formatted(hours, minutes);
	}

	/**
	 * Sets the current time.<br>
	 * Time has to be a string and have hh:mm pattern. If the hour and/or the minute values are more than 23 or 59
	 * respectively they are assigned the maximum values. If the format or the pattern don't match the current time
	 * doesn't change.
	 * @param o string representation of time
	 */
	@Override
	public void setValue(Object o) {
		try {
			int newHours;
			int newMinutes;
			if (o instanceof String s) {
				newHours = Integer.parseInt(s.substring(0, s.indexOf(":")));
				newHours = Math.max(Math.min(23, newHours), 0);
				newMinutes = Integer.parseInt(s.substring(s.indexOf(":") + 1));
				newMinutes = Math.max(Math.min(59, newMinutes), 0);
			} else return;
			hours = newHours;
			minutes = newMinutes;
			for (ChangeListener changeListener : getChangeListeners()) {
				changeListener.stateChanged(new ChangeEvent(this));
			}
		} catch (IllegalArgumentException | IndexOutOfBoundsException ignored) { }
	}

	/**
	 * Return the current time incremented by 1 minute. If the current time is 23:59 it returns 00:00.
	 * @return current time incremented by 1 minutes
	 */
	@Override
	public Object getNextValue() {
		if (hours == 23 && minutes == 59) return "00:00";
		else if (minutes == 59) return "%02d:00".formatted(hours + 1);
		else return "%02d:%02d".formatted(hours, minutes + 1);
	}

	/**
	 * Return the current time decremented by 1 minute. If the current time is 00:00 it returns 23:59.
	 * @return current time decremented by 1 minute
	 */
	@Override
	public Object getPreviousValue() {
		if (hours == 0 && minutes == 0) return "23:59";
		else if (minutes == 0) return "%02d:59".formatted(hours - 1);
		else return "%02d:%02d".formatted(hours, minutes - 1);
	}

	/**
	 * Returns the hour value ranging from 0 to 23.
	 * @return hour value
	 */
	public int getHours() {
		return hours;
	}

	/**
	 * Returns the minute value ranging from 0 to 59.
	 * @return minute value
	 */
	public int getMinutes() {
		return minutes;
	}
}
