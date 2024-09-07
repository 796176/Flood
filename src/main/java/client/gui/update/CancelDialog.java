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

import javax.swing.*;
import java.awt.*;

/**
 * CancelDialog is a module dialog with an option to interrupt the set process.
 */
public class CancelDialog extends JDialog {
	private final JButton cancelButton;

	/**
	 * Constructs CancelDialog
	 * @param parent reference to a parent {@link JFrame}
	 */
	public CancelDialog(JFrame parent) {
		super(parent, "Retrieving");

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(800, 400);
		int x = parent.getLocation().x + (parent.getWidth() - getWidth()) / 2;
		int y = parent.getLocation().y + (parent.getHeight() - getHeight()) / 2;
		setLocation(x, y);

		GridBagLayout bagLayout = new GridBagLayout();
		setLayout(bagLayout);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weighty = 10;
		JLabel label = new JLabel("Please wait");
		bagLayout.setConstraints(label, constraints);
		add(label);

		constraints.weighty = 1;
		cancelButton = new JButton("Cancel");
		bagLayout.setConstraints(cancelButton, constraints);
		add(cancelButton);
	}

	/**
	 * Sets the thread to be interrupted
	 * @param t tread to be interrupted
	 */
	public void setRunnableThread(Thread t) {
		cancelButton.addActionListener(actionEvent -> t.interrupt());
	}

	@Override
	public void setVisible(boolean visible) {
		getOwner().setEnabled(!visible);
		super.setVisible(visible);
	}
}
