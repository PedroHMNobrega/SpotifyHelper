package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import backup.BackupFrame;
import gui.Colors;
import gui.Components;
import searchTracks.SearchTracksFrame;
import utils.Functions;

/**
 * Responsible for the GUI of the app.
 * 
 * @author Pedro Nóbrega
 *
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 900;
	public static final int HEIGHT = 500;
	
	private JPanel mainPanel, panelButtons;
	private BackupFrame backupFrame;
	private SearchTracksFrame searchTracksFrame;
	private Functions functions;
	
	/**
	 * Construct the app.
	 */
	public MainFrame(String title) {
		super(title);
		this.mainPanel = new JPanel();
		this.panelButtons = new JPanel();
		
		this.backupFrame = new BackupFrame(mainPanel, panelButtons, this);
		this.searchTracksFrame = new SearchTracksFrame(mainPanel, panelButtons, this);
		this.functions = new Functions();
				
		this.setResizable(false);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Icon
		Image img = functions.getIconImage("/icon.png");
		this.setIconImage(img);
		
		menuScreen();
		
		this.setVisible(true);
	}
	
	/**
	 * Create the menu screen.
	 */
	private void menuScreen() {
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(Colors.GREEN);
		
		JButton searchArtistTracks = Components.createButton("Search Aritst Tracks", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainPanel.removeAll();
				Components.panelRefresh(mainPanel);
				searchTracksFrame.searchTracksScreen();
			}
		});
		JButton backUp = Components.createButton("Saved Tracks Back Up", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.removeAll();
				Components.panelRefresh(mainPanel);
				backupFrame.backUpScreen();
			}
		});
		
		mainPanel.add(searchArtistTracks);
		mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		mainPanel.add(backUp);
		
		//Adding components to the Frame
		this.getContentPane().add(BorderLayout.CENTER, mainPanel);
	}
	
	/**
	 * Go to the app menu.
	 */
	public void goToMenu() {
		mainPanel.removeAll();
		Components.panelRefresh(mainPanel);
		panelButtons.removeAll();
		Components.panelRefresh(panelButtons);
		menuScreen();
	}
}
