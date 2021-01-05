package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 * Responsible for the GUI components.
 * 
 * @author Pedro Nóbrega
 *
 */
public class Components {
	
	/**
	 * Refresh the panel.
	 * 
	 * @param panel
	 */
	public static void panelRefresh(JPanel panel) {
		panel.revalidate();
		panel.repaint();
	}
	
	/**
	 * Create a JButton based on the title and the ActionListener.
	 * 
	 * @param title
	 * @param al
	 * @return the JButton
	 */
	public static JButton createButton(String title, ActionListener al) {
		JButton bt = new JButton(title);
		bt.setBackground(Colors.GRAY);
		bt.setForeground(Color.white);
		bt.setBorder(new LineBorder(Colors.GRAY, 10, false));
		bt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		bt.addActionListener(al);
		return bt;
	}
	
	/**
	 * Create a JLabel based on the title, color, font and xAlignment.
	 * 
	 * @param title
	 * @param color
	 * @param font
	 * @param xAlignment
	 * @return the JLabel
	 */
	public static JLabel createLabel(String title, Color color, Font font, float xAlignment) {
		JLabel label = new JLabel(title);
		label.setForeground(color);
		label.setFont(font);
		label.setAlignmentX(xAlignment);
		return label;
	}
	
	/**
	 * Create a JTextField based on the Dimension and xAlignment.
	 * 
	 * @param size
	 * @param xAlignment
	 * @return the JTextField
	 */
	public static JTextField createTextField(Dimension size, float xAlignment) {
		JTextField tf = new JTextField();
		tf.setPreferredSize(size);
		tf.setMaximumSize(tf.getPreferredSize());
		tf.setAlignmentX(xAlignment);
		tf.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tf.requestFocus();
				
			}
		});
		return tf;
	}
	
	/**
	 * Create a JScrollPane without border.
	 * 
	 * @param panel
	 * @return the JScrollPane
	 */
	public static JScrollPane createScroller(JPanel panel) {
		JScrollPane scroller = new JScrollPane(panel);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		return scroller;
	}
	
	/**
	 * Create a modeless JDialog.
	 * 
	 * @param frame
	 * @param title
	 * @param text
	 * @return the JDialog
	 */
	public static JDialog createDialog(JFrame frame, String title, String text) {
		final JDialog dialog = new JDialog(frame, title, ModalityType.MODELESS);
		dialog.setSize(200, 45);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(frame);
		return dialog;
	}
	
	/**
	 * Display a message in a JPanel.
	 * 
	 * @param panel
	 * @param message
	 */
	public static void showMessage(JPanel panel, String message) {
		panel.removeAll();
		panelRefresh(panel);
		JLabel messageLabel = Components.createLabel(message, Color.white, new Font("arial",Font.BOLD, 20), Component.CENTER_ALIGNMENT);
		panel.add(Box.createVerticalGlue());
		panel.add(messageLabel);
		panel.add(Box.createVerticalGlue());
	}
	
}
