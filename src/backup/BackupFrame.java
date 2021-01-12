package backup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import gui.Colors;
import gui.Components;
import main.MainFrame;
import utils.Functions;
import utils.Track;

/**
 * Responsible for the GUI of backup.
 * 
 * @author Pedro Nóbrega
 *
 */
public class BackupFrame {
	
	/**
	 * The url to get the access token.
	 */
	static final String TOKEN_URL = "https://accounts.spotify.com/en/authorize?response_type=token&client_id=adaaf209fb064dfab873a71817029e0d&redirect_uri=https:%2F%2Fdeveloper.spotify.com%2Fdocumentation%2Fweb-playback-sdk%2Fquick-start%2F&scope=streaming%20user-read-email%20user-modify-playback-state%20user-read-private%20user-library-read&show_dialog=true";
	
	private MainFrame mainFrame;
	private JPanel mainPanel, panelButtons;
	private BackupFunctions backupFunctions;
	
	/**
	 * Construct the Backup Frame
	 * 
	 * @param mainPanel
	 * @param panelButtons
	 * @param mainFrame
	 */
	public BackupFrame(JPanel mainPanel, JPanel panelButtons, MainFrame mainFrame) {
		this.mainPanel = mainPanel;
		this.panelButtons = panelButtons;
		this.mainFrame = mainFrame;
		this.backupFunctions = new BackupFunctions();
	}
	
	/**
	 * Create the backup screen.
	 */
	public void backUpScreen() {
		//Input
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(Colors.GREEN);
		
		JLabel label = Components.createLabel("Enter your Token", Color.white, 
				new Font("arial",Font.BOLD, 17), Component.CENTER_ALIGNMENT);
		JTextField tokenTextField = Components.createTextField(new Dimension(300, 30), Component.CENTER_ALIGNMENT);
		
		mainPanel.add(Box.createVerticalGlue());
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mainPanel.add(tokenTextField);
		mainPanel.add(Box.createVerticalGlue());
		
		//Buttons
		panelButtons.setPreferredSize(new Dimension(MainFrame.WIDTH, 100));
		panelButtons.setBackground(Colors.GREEN);
		panelButtons.setLayout(new GridBagLayout());
		
		JButton send = Components.createButton("Enter", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				enterButtonAction(tokenTextField);
			}
		});
		JButton getToken = Components.createButton("Get Token", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Functions.browserRedirect(TOKEN_URL);
			}
		});
		JButton menu = Components.createButton("Menu", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.goToMenu();
			}
		});
		
		panelButtons.add(send);
		panelButtons.add(Box.createRigidArea(new Dimension(5, 0)));
		panelButtons.add(getToken);
		panelButtons.add(Box.createRigidArea(new Dimension(5, 0)));
		panelButtons.add(menu);
		
		//Adding components to the Frame
		mainFrame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		mainFrame.getContentPane().add(BorderLayout.SOUTH, panelButtons);
	}
	
	/**
	 * Search the user's Spotify saved tracks and show the user the option to save it or compare
	 * with another track list.
	 * 
	 * @param tokenTextField
	 */
	private void enterButtonAction(JTextField tokenTextField) {		
		final JDialog dialog = Components.createDialog(mainFrame, "Loading...", "Loading, wait!");
		dialog.setVisible(true);
		
		String accessToken = tokenTextField.getText();
		SwingWorker<ArrayList<Track>, Void> worker = new SwingWorker<ArrayList<Track>, Void>() {
			@Override
			protected ArrayList<Track> doInBackground() throws Exception {
				return backupFunctions.getTrackList(accessToken);
			}

		    @Override
		    protected void done() {
		        dialog.setVisible(false);
		    }

		};
		worker.execute();
		
		ArrayList<Track> trackList;
		try {
			trackList = worker.get();
			
			Components.showMessage(mainPanel, trackList.size() + " tracks found!");
			
			panelButtons.removeAll();
			Components.panelRefresh(panelButtons);
			
			JButton save = Components.createButton("Save Tracks", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveButtonAction(trackList);
				}
			});
			JButton compare = Components.createButton("Compare", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					compareButtonAction(trackList);
				}
			});
			JButton menu = Components.createButton("Menu", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mainFrame.goToMenu();
				}
			});
			panelButtons.add(save);
			panelButtons.add(Box.createRigidArea(new Dimension(5, 0)));
			panelButtons.add(compare);
			panelButtons.add(Box.createRigidArea(new Dimension(5, 0)));
			panelButtons.add(menu);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid Token", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Save the tracks in a CSV file and show a success message to the user.
	 * 
	 * @param tracks
	 */
	private void saveButtonAction(ArrayList<Track> tracks) {
		if(backupFunctions.saveFile(mainFrame, tracks)) {
			Components.showMessage(mainPanel, "Tracks Saved");
		}
	}
	
	/**
	 * Compare the current trackList with a backUp trackList chose by the user and shows the difference between the lists. 
	 * 
	 * @param trackList
	 */
	private void compareButtonAction(ArrayList<Track> trackList) {
		File file = backupFunctions.getFile(mainFrame);
		if(file != null) {
			HashMap<String, ArrayList<Track>> comp = backupFunctions.compareBackup(file, trackList);
			if(comp != null) {
				mainPanel.removeAll();
				Components.panelRefresh(mainPanel);
				
				JPanel scrollPanel = new JPanel();
				scrollPanel.setBackground(Colors.GREEN);
				scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
				
				JScrollPane scroller = Components.createScroller(scrollPanel);
				mainPanel.add(scroller);
				
				if(comp.get("new").size() == 0 && comp.get("removed").size() == 0) {
					Components.showMessage(mainPanel, "Nothing has changed!");
				} else {
					int maxSize = 90;	
					showList(scrollPanel, comp.get("new"), "New Tracks", maxSize);
					showList(scrollPanel, comp.get("removed"), "Removed Tracks", maxSize);
				}
			} else Components.showMessage(mainPanel, "An error occurred. Try Again!");			
		}
	}
	
	
	/**
	 * Shows in the screen a trackList.
	 * 
	 * @param panel
	 * @param trackList
	 * @param title
	 * @param maxStringSize
	 */
	private void showList(JPanel panel, ArrayList<Track> trackList, String title, int maxStringSize) {
		if(trackList.size() != 0) {
			JLabel newLabel = Components.createLabel(title, Color.DARK_GRAY, 
					new Font("arial",Font.BOLD, 20), Component.CENTER_ALIGNMENT);
			panel.add(Box.createRigidArea(new Dimension(0, 10)));
			panel.add(newLabel);
			panel.add(Box.createRigidArea(new Dimension(0, 10)));
			for(Track t: trackList) {
				String trackString = t.getName() + " | " + t.getArtists()[0];
				int lastIdx = trackString.length();
				JLabel auxLabel = Components.createLabel(trackString.substring(0, Math.min(maxStringSize, lastIdx)), 
						Color.white, new Font("arial",Font.ITALIC, 18), Component.CENTER_ALIGNMENT);
				panel.add(auxLabel);
				panel.add(Box.createRigidArea(new Dimension(0, 5)));
			}			
		}
	}
}
