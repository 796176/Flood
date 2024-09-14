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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * DateEditor is an editor for {@link JSpinner} whose model is {@link SpinnerDateModel}.
 * The set of displayed characters is limited to digits and a period ( . ).
 */
public class DateEditor extends JSpinner.DefaultEditor {
	/**
	 * Constructs a DateEditor editor that supports displaying and editing the value of a SpinnerDateModel with a JFormattedTextField.
	 * @param spinner spinner whose model this editor will monitor
	 */
	public DateEditor(JSpinner spinner) {
		super(spinner);

		spinner.getModel().addChangeListener(changeEvent -> {
			if (changeEvent.getSource() instanceof SpinnerDateModel sdm) {
				getTextField().setValue(sdm.getValue());
			}
		});

		getTextField().addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				StringBuilder text = new StringBuilder(getTextField().getText());
				int caret = getTextField().getCaretPosition();
				if (e.getKeyChar() == 8 && caret > 0) { //backspace key
					text.deleteCharAt(getTextField().getCaretPosition() - 1);
					getTextField().setText(text.toString());
					getTextField().setCaretPosition(caret - 1);
				} else if (Character.isDigit(e.getKeyChar()) || e.getKeyChar() == '.') {
					text.insert(getTextField().getCaretPosition(), e.getKeyChar());
					getTextField().setText(text.toString());
					getTextField().setCaretPosition(caret + 1);
				} else if (e.getKeyChar() == '\n') {
					spinner.getModel().setValue(text.toString());
					getTextField().setText(spinner.getModel().getValue().toString());
				}
			}
		});
	}
}
