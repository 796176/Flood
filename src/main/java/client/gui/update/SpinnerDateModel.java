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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * SpinnerDateModel is a model for {@link JSpinner} to display and edit date.
 * Date is presented as a string and has dd.mm.yyyy pattern.
 * For Spinner to support SpinnerDateModel it has to have {@link DateEditor} as its editor.
 */
public class SpinnerDateModel extends AbstractSpinnerModel {
	private final GregorianCalendar calendar = new GregorianCalendar();

	/**
	 * Returns a string representation of date with dd.mm.yyyy pattern.
	 * @return a string representation of date
	 */
	@Override
	public Object getValue() {
		return getFormattedDate(calendar);
	}

	/**
	 * Sets the current value of date.<br>
	 * Date has to be a string and have dd.mm.yyyy pattern. If the day, and/or the month, and/or the year
	 * values are outside the specified boundaries they are assigned the lowest or the biggest value if they are under
	 * or over the limits respectively.<br><br>
	 * The year value is confined within a range from 1970 to 2999.<br>
	 * The month value is confined within a range from 1 to 12.<br>
	 * The day value is confined within a range from 1 to 28, or 29, or 30, or 31 depending on the month and
	 * if it's a leap year or not.<br><br>
	 * @param o string representation of date
	 */
	@Override
	public void setValue(Object o) {
		try {
			int day;
			int month;
			int year;
			if (o instanceof String s) {
				int firstPeriod = s.indexOf('.');
				int secondPeriod = s.indexOf('.', firstPeriod + 1);
				day = Integer.parseInt(s.substring(0, firstPeriod));
				month = Integer.parseInt(
					s.substring(
						firstPeriod + 1,
						secondPeriod
					)
				);
				month = Math.max(Math.min(month, 12), 1);
				year = Integer.parseInt(s.substring(secondPeriod + 1));
				year = Math.max(Math.min(year, 2999), 1970);
			} else return;
			DateTimeFormatter.ofPattern("dd.MM.yyyy");
			day = Math.max(Math.min(day, YearMonth.of(year, month).lengthOfMonth()), 1);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.MONTH, month - 1); //month index starts with 0
			calendar.set(Calendar.YEAR, year);
			for (ChangeListener changeListener: getChangeListeners()) {
				changeListener.stateChanged(new ChangeEvent(this));
			}
		} catch (IllegalArgumentException | IndexOutOfBoundsException ignored) {}
	}

	/**
	 * Returns the current date incremented by 1 day.
	 * @return current date incremented by 1 day
	 */
	@Override
	public Object getNextValue() {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) calendar.clone();
		gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
		return getFormattedDate(gregorianCalendar);
	}

	/**
	 * Returns the current date decremented by 1 day.
	 * @return current date decremented by 1 day
	 */
	@Override
	public Object getPreviousValue() {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) calendar.clone();
		gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
		return getFormattedDate(gregorianCalendar);
	}

	private String getFormattedDate(Calendar calendar) {
		//month index starts with 0
		return "%02d.%02d.%d"
			.formatted(
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.YEAR)
			);
	}

	/**
	 * Returns the year value.
	 * @return year value
	 */
	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * Returns the month value
	 * @return month value
	 */
	public int getMonth() {
		return calendar.get(Calendar.MONTH) + 1; //month index starts with 0
	}

	/**
	 * Returns the day value.
	 * @return day value
	 */
	public int getDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
}
