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

package client.gui.settings;

import client.SettingManipulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * SettingContentPane is the content pane of {@link SettingsDialog}
 */
class SettingContentPane extends Container {
	private JTextField connectionTextField;
	private JTextField proxyProtocolTextField;
	private JTextField proxyURLTextField;
	private JTextField proxyPortTextField;
	SettingsDialog sd;
	SettingContentPane(SettingsDialog settingsDialog) {
		assert settingsDialog != null;

		sd = settingsDialog;
		try {
			BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
			setLayout(bl);
			Dimension elementOffset = new Dimension(0, 10);
			Dimension groupOffset = new Dimension(0, 40);

			JLabel connectionLabel = new JLabel("Remote host:");
			connectionLabel.setMaximumSize(connectionLabel.getMinimumSize());
			connectionLabel.setPreferredSize(connectionLabel.getMinimumSize());
			connectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(connectionLabel);

			add(Box.createRigidArea(elementOffset));

			connectionTextField =
				new JTextField(SettingManipulator.getValue(SettingManipulator.Parameter.REMOTE_URL).orElse(""));
			connectionTextField
				.setMaximumSize(new Dimension(Integer.MAX_VALUE, connectionTextField.getMinimumSize().height));
			connectionTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(connectionTextField);

			add(Box.createRigidArea(groupOffset));

			JLabel proxyProtocolLabel = new JLabel("Proxy protocol:");
			proxyProtocolLabel.setMaximumSize(proxyProtocolLabel.getMinimumSize());
			proxyProtocolLabel.setPreferredSize(proxyProtocolLabel.getMinimumSize());
			proxyProtocolLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(proxyProtocolLabel);

			add(Box.createRigidArea(elementOffset));

			proxyProtocolTextField = new JTextField();
			proxyProtocolTextField
				.setText(SettingManipulator.getValue(SettingManipulator.Parameter.PROXY_PROTOCOL).orElse(""));
			proxyProtocolTextField
				.setMaximumSize(new Dimension(Integer.MAX_VALUE, proxyProtocolTextField.getMinimumSize().height));
			proxyProtocolTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(proxyProtocolTextField);

			add(Box.createRigidArea(elementOffset));

			JLabel proxyURLLabel = new JLabel("Proxy URL:");
			proxyURLLabel.setMaximumSize(proxyURLLabel.getMinimumSize());
			proxyURLLabel.setPreferredSize(proxyURLLabel.getMinimumSize());
			proxyURLLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(proxyURLLabel);

			add(Box.createRigidArea(elementOffset));

			proxyURLTextField = new JTextField();
			proxyURLTextField
				.setText(SettingManipulator.getValue(SettingManipulator.Parameter.PROXY_URL).orElse(""));
			proxyURLTextField
				.setMaximumSize(new Dimension(Integer.MAX_VALUE, proxyURLTextField.getMinimumSize().height));
			proxyURLTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(proxyURLTextField);

			add(Box.createRigidArea(elementOffset));

			JLabel proxyPortLabel = new JLabel("Proxy port");
			proxyPortLabel.setMaximumSize(proxyPortLabel.getMinimumSize());
			proxyPortLabel.setPreferredSize(proxyPortLabel.getMinimumSize());
			proxyPortLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(proxyPortLabel);

			add(Box.createRigidArea(elementOffset));

			proxyPortTextField = new JTextField();
			proxyPortTextField
				.setText(SettingManipulator.getValue(SettingManipulator.Parameter.PROXY_PORT).orElse(""));
			proxyPortTextField
				.setMaximumSize(new Dimension(Integer.MAX_VALUE, proxyPortTextField.getMinimumSize().height));
			proxyPortTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(proxyPortTextField);

			add(Box.createRigidArea(groupOffset));

			JPanel saveButtonPad = new JPanel();
			saveButtonPad.setAlignmentX(Component.LEFT_ALIGNMENT);
			saveButtonPad.setLayout(new FlowLayout());
			JButton saveButton = new JButton("Save");
			saveButtonPad.add(saveButton);
			saveButton.addActionListener(new SaveButtonListener());
			add(saveButtonPad);
		} catch (IOException exception) {
			JOptionPane.showMessageDialog(
				this,
				exception.getMessage(),
				"IOException",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}

	private class SaveButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			try {
				SettingManipulator.setValue(SettingManipulator.Parameter.REMOTE_URL, connectionTextField.getText());
				SettingManipulator.setValue(SettingManipulator.Parameter.PROXY_PROTOCOL, proxyProtocolTextField.getText());
				SettingManipulator.setValue(SettingManipulator.Parameter.PROXY_URL, proxyURLTextField.getText());
				SettingManipulator.setValue(SettingManipulator.Parameter.PROXY_PORT, proxyPortTextField.getText());
			} catch (IOException exception) {
				JOptionPane.showMessageDialog(
					SettingContentPane.this,
					exception.getMessage(),
					"IOException",
					JOptionPane.ERROR_MESSAGE
				);
			} finally {
				sd.setVisible(false);
			}
		}
	}
}
