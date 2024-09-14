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
 * AboutContentPane is the content pane of {@link AboutDialog}.
 */
class AboutContentPane extends JScrollPane{
	AboutContentPane(AboutDialog ad) {
		super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		assert ad != null;

		JTextArea textArea = new JTextArea(
			"""
				Flood
				Version 0.5.0
				
				Flood GUI client is a frontend for Flood network inspection tool.
				
				Official page: github.com/796176/flood
				
				Licence: GPL3 www.gnu.org/licenses/gpl-3.0.en.html
				
				Author: Yegore Vlussove
				
				Flatlaf: github.com/JFormDesigner/FlatLaf
				Json: github.com/stleary/JSON-java
				""");
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		setViewportView(textArea);
	}
}
