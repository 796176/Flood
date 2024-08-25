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

package client.gui.main;

import client.gui.about.AboutDialog;
import client.gui.settings.SettingsDialog;
import client.gui.update.UpdateDialog;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MainFrameMenu is the menu of {@link MainFrame}.
 */
class MainFrameMenu extends JMenuBar {
	MainFrameMenu(MainFrame parent) {
		assert parent != null;

		JMenu tools = new JMenu("Tools");
		JMenuItem update = new JMenuItem("Update");
		update.addActionListener(actionEvent -> new UpdateDialog(parent));
		tools.add(update);
		add(tools);

		JMenu options = new JMenu("Options");
		options.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				JMenu menu = (JMenu) mouseEvent.getSource();
				menu.setSelected(false);
				new SettingsDialog(parent);
			}
		});
		add(options);

		JMenu aboutMenu = new JMenu("About");
		aboutMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				JMenu menu = (JMenu) mouseEvent.getSource();
				menu.setSelected(false);
				new AboutDialog(parent);
			}
		});
		add(aboutMenu);
	}
}
