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

package client.gui.about;

import javax.swing.*;

/**
 * AboutDialog displays the information about Flood Client.
 */
public class AboutDialog extends JDialog {
	public AboutDialog(JFrame parent) {
		super(parent, "About", true);
		setSize(1200, 1400);
		int x = parent.getLocation().x + (parent.getWidth() - getWidth()) / 2;
		int y = parent.getLocation().y + (parent.getHeight() - getHeight()) / 2;
		setLocation(x, y);
		setContentPane(new AboutContentPane(this));
		setVisible(true);
	}
}
