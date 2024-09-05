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
import java.util.GregorianCalendar;

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
				parameters.setPopupVisible(false);
				updateDialog.updateRequest(RequestType.retrieve_last, ((UserChoice) itemEvent.getItem()).params);
			}
		});
		requestTab.add(parameters);
		addTab("Recent", requestTab);


		GridBagLayout bagLayout = new GridBagLayout();
		JPanel requestRangeTab = new JPanel(bagLayout);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 20, 50);
		JLabel fromLabel = new JLabel("From");
		bagLayout.setConstraints(fromLabel, constraints);
		requestRangeTab.add(fromLabel);

		constraints.gridx = 1;
		constraints.gridy = 0;
		JSpinner fromTimeSpinner = new JSpinner(new SpinnerTimeModel());
		fromTimeSpinner.setEditor(new TimeEditor(fromTimeSpinner));
		bagLayout.setConstraints(fromTimeSpinner, constraints);
		requestRangeTab.add(fromTimeSpinner);

		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 20, 0);
		JSpinner fromDateSpinner = new JSpinner(new SpinnerDateModel());
		fromDateSpinner.setEditor(new DateEditor(fromDateSpinner));
		bagLayout.setConstraints(fromDateSpinner, constraints);
		requestRangeTab.add(fromDateSpinner);


		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 0, 0, 50);
		JLabel toLabel = new JLabel("To");
		bagLayout.setConstraints(toLabel, constraints);
		requestRangeTab.add(toLabel);

		constraints.gridx = 1;
		constraints.gridy = 1;
		JSpinner toTimeSpinner = new JSpinner(new SpinnerTimeModel());
		toTimeSpinner.setEditor(new TimeEditor(toTimeSpinner));
		bagLayout.setConstraints(toTimeSpinner, constraints);
		requestRangeTab.add(toTimeSpinner);

		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 0, 0, 0);
		JSpinner toDateSpinner = new JSpinner(new SpinnerDateModel());
		toDateSpinner.setEditor(new DateEditor(toDateSpinner));
		bagLayout.setConstraints(toDateSpinner, constraints);
		requestRangeTab.add(toDateSpinner);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(50, 0, 0, 0);
		JButton requestButton = new JButton("Request");
		requestButton.addActionListener(actionEvent -> {
			GregorianCalendar startDate = new GregorianCalendar(
				((SpinnerDateModel) fromDateSpinner.getModel()).getYear(),
				((SpinnerDateModel) fromDateSpinner.getModel()).getMonth() - 1,
				((SpinnerDateModel) fromDateSpinner.getModel()).getDay(),
				((SpinnerTimeModel) fromTimeSpinner.getModel()).getHours(),
				((SpinnerTimeModel) fromTimeSpinner.getModel()).getMinutes()
			);

			GregorianCalendar endDate = new GregorianCalendar(
				((SpinnerDateModel) toDateSpinner.getModel()).getYear(),
				((SpinnerDateModel) toDateSpinner.getModel()).getMonth() - 1,
				((SpinnerDateModel) toDateSpinner.getModel()).getDay(),
				((SpinnerTimeModel) toTimeSpinner.getModel()).getHours(),
				((SpinnerTimeModel) toTimeSpinner.getModel()).getMinutes()
			);

			if (startDate.compareTo(endDate) > 0) {
				JOptionPane
					.showMessageDialog(
						updateDialog,
						"The starting date can't be more recent the finishing date",
						"Error",
						JOptionPane.ERROR_MESSAGE
					);
				return;
			}

			String params = "?s=" + startDate.getTimeInMillis() + "&e=" + endDate.getTimeInMillis();
			updateDialog.updateRequest(RequestType.retrieve_range, params);
		});
		bagLayout.setConstraints(requestButton, constraints);
		requestRangeTab.add(requestButton);

		addTab("Range", requestRangeTab);
	}

	private record UserChoice(String displayed, String params) {
		@Override
		public String toString() {
			return displayed;
		}
	}
}
