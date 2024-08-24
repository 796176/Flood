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

import client.SettingManipulator;
import client.gui.FRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * MainFrame is the main window of Flood Client.
 */
public class MainFrame extends JFrame {
	private FRecord[] records = new FRecord[]{};
	private Container prevGraph;
	public MainFrame() {
		try {
			setSize(
				Integer.parseInt(
					SettingManipulator.getValue(SettingManipulator.Parameter.MAIN_WINDOW_WIDTH).orElse("500")
				),
				Integer.parseInt(
					SettingManipulator.getValue(SettingManipulator.Parameter.MAIN_WINDOW_HEIGHT).orElse("500")
				)
			);

			setLocation(
				Integer.parseInt(
					SettingManipulator.getValue(SettingManipulator.Parameter.MAIN_WINDOW_X).orElse("0")
				),
				Integer.parseInt(
					SettingManipulator.getValue(SettingManipulator.Parameter.MAIN_WINDOW_Y).orElse("0")
				)
			);
		} catch (IOException exception) {
			JOptionPane.showMessageDialog(
				this,
				exception.getMessage(),
				"IOException",
				JOptionPane.ERROR_MESSAGE
			);
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(new MainFrameContentPane(this));

		setJMenuBar(new MainFrameMenu(this));

		prevGraph = new JLabel("Use Tools > Update to draw the graph");
		add(prevGraph);
		setVisible(true);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (records.length > 0) {
					Graph newGraph = new Graph(records, getWidth(), getHeight());
					prevGraph.setVisible(false);
					MainFrame.this.add(newGraph);
					prevGraph = newGraph;
					repaint(100);
				}
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			try {
				SettingManipulator.setValue(
					SettingManipulator.Parameter.MAIN_WINDOW_X, Integer.toString(MainFrame.this.getLocation().x)
				);
				SettingManipulator.setValue(
					SettingManipulator.Parameter.MAIN_WINDOW_Y, Integer.toString(MainFrame.this.getLocation().y)
				);
				SettingManipulator.setValue(
					SettingManipulator.Parameter.MAIN_WINDOW_HEIGHT, Integer.toString(MainFrame.this.getHeight())
				);
				SettingManipulator.setValue(
					SettingManipulator.Parameter.MAIN_WINDOW_WIDTH, Integer.toString(MainFrame.this.getWidth())
				);
			} catch (IOException ignored) { }
			}
		});
	}

	/**
	 * Sets the records to be displayed. The amount and the precision of the records isn't guaranteed.
	 * @param records records to be displayed
	 */
	public void setRecords(FRecord[] records) {
		assert records != null;

		Graph graph = new Graph(records,
			getWidth() - getInsets().left - getInsets().right,
			getHeight() - getInsets().top - getInsets().bottom - getJMenuBar().getHeight());
		prevGraph.setVisible(false);
		add(graph);
		prevGraph = graph;
		this.records = records;
		repaint();
	}
}
