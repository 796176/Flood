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

package client.gui.update;

import global.RequestType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Calendar;

/**
 * UpdateContentPane is the content pane of {@link UpdateDialog}.
 */
class UpdateContentPane extends JTabbedPane {
	UpdateContentPane(UpdateDialog updateDialog) {
		assert updateDialog != null;

		JPanel requestTab = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel label = new JLabel("Request all the records for the past:");
		requestTab.add(label);
		JComboBox<UserChoice> parameters = new JComboBox<>(new UserChoice[]{
			new UserChoice("1 hour", "?u=%d&t=%d".formatted(Calendar.HOUR, 1)),
			new UserChoice("24 hours", "?u=%d&t=%d".formatted(Calendar.HOUR, 24)),
			new UserChoice("7 days", "?u=%d&t=%d".formatted(Calendar.DATE, 7)),
			new UserChoice("14 days", "?u=%d&t=%d".formatted(Calendar.DATE, 14)),
			new UserChoice("30 days", "?u=%d&t=%d".formatted(Calendar.DATE, 30)),
		});
		parameters.setSelectedItem(null);
		parameters.addItemListener(itemEvent -> {

			if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
				updateDialog.updateRequest(RequestType.retrieve_last, ((UserChoice) itemEvent.getItem()).params);
			}
		});
		requestTab.add(parameters);
		addTab("Recent", requestTab);
	}

	private record UserChoice(String displayed, String params) {
		@Override
		public String toString() {
			return displayed;
		}
	}
}
