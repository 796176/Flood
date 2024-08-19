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

import javax.swing.*;

/**
 * SettingDialog allows users to set up Flood Client via GUI.
 */
public class SettingsDialog extends JDialog {
	public SettingsDialog(JFrame frame) {
		super(frame, "Options", true);
		setSize(400, 800);
		int x = frame.getLocation().x + (frame.getWidth() - getWidth()) / 2;
		int y = frame.getLocation().y + (frame.getHeight() - getHeight()) / 2;
		setLocation(x, y);
		setContentPane(new SettingContentPane(this));
		setVisible(true);
	}
}
