package searchTracks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

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
import utils.Artist;
import utils.Functions;
import utils.Track;

/**
 * Responsible for the GUI of search tracks.
 * 
 * @author Pedro Nóbrega
 *
 */
public class SearchTracksFrame {
	private MainFrame mainFrame;
	private JPanel mainPanel, panelButtons;
	private SearchTracksFunctions searchTracksFunctions; 
	
	/**
	 * Construct the SearchTracks Frame
	 * 
	 * @param mainPanel
	 * @param panelButtons
	 * @param mainFrame
	 */
	public SearchTracksFrame(JPanel mainPanel, JPanel panelButtons, MainFrame mainFrame) {
		this.mainPanel = mainPanel;
		this.panelButtons = panelButtons;
		this.mainFrame = mainFrame;
		this.searchTracksFunctions = new SearchTracksFunctions();
	}

	/**
	 * Show the text field to search for an artist.
	 */
	public void searchTracksScreen() {
		//Input
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(Colors.GREEN);
		
		JLabel label = Components.createLabel("Search for an Artist", Color.white,
				new Font("arial",Font.BOLD, 17), Component.CENTER_ALIGNMENT);
		JTextField artistTextField = Components.createTextField(new Dimension(300, 30), Component.CENTER_ALIGNMENT);
		
		mainPanel.add(Box.createVerticalGlue());
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mainPanel.add(artistTextField);
		mainPanel.add(Box.createVerticalGlue());
		
		//Buttons
		panelButtons.setPreferredSize(new Dimension(MainFrame.WIDTH, 100));
		panelButtons.setBackground(Colors.GREEN);
		panelButtons.setLayout(new GridBagLayout());
		
		JButton search = Components.createButton("Search", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButtonAction(artistTextField);
			}
		});
		JButton menu = Components.createButton("Menu", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.goToMenu();
			}
		});
		
		panelButtons.add(search);
		panelButtons.add(Box.createRigidArea(new Dimension(5, 0)));
		panelButtons.add(menu);
		
		//Adding components to the frame
		mainFrame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		mainFrame.getContentPane().add(BorderLayout.SOUTH, panelButtons);
	}
	
	/**
	 * Search for all the artists that match with the input.
	 * 
	 * @param tf
	 */
	private void searchButtonAction(JTextField tf) {
		final JDialog dialog = Components.createDialog(mainFrame, "Searching...", "Searching, wait!");
		dialog.setVisible(true);
		
		String artistName = tf.getText();
		SwingWorker<ArrayList<Artist>, Void> worker = new SwingWorker<ArrayList<Artist>, Void>() {
			@Override
			protected ArrayList<Artist> doInBackground() throws Exception {
				return searchTracksFunctions.searchArtists(artistName);
			}
			
			@Override
			protected void done() {
				dialog.setVisible(false);
			}
			
		};
		worker.execute();
		
		ArrayList<Artist> artists;
		try {
			artists = worker.get();
			showArtists(artists);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "An error occurred. Try Again", "Error", JOptionPane.ERROR_MESSAGE);
		}	
	}
	
	/**
	 * Show all the artists found to the user.
	 * 
	 * @param artists
	 */
	private void showArtists(ArrayList<Artist> artists) throws NullPointerException{
		if(artists == null) 
			throw new NullPointerException();
		
		mainPanel.removeAll();
		Components.panelRefresh(mainPanel);
		panelButtons.removeAll();
		Components.panelRefresh(panelButtons);
		
		if(artists.size() == 0) {
			Components.showMessage(mainPanel, "Nothing found!");
		} else {
			JLabel label = Components.createLabel(artists.size() + " Artists Found", Color.white, 
					new Font("arial",Font.BOLD, 20), Component.CENTER_ALIGNMENT);
			mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			mainPanel.add(label);
			mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			
			JPanel scrollPanel = new JPanel();
			scrollPanel.setBackground(Colors.GREEN);
			scrollPanel.setLayout(new GridLayout(artists.size(), 1));
			JScrollPane scroller = Components.createScroller(scrollPanel);
			mainPanel.add(scroller);
			
			for(int i = 0; i < artists.size(); i++) {
				Artist artist = artists.get(i);
				JButton artistButton = Components.createButton(artist.getName() + " (" + artist.getPopularity() + ")", new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						selectArtistButtonAction(artist);
					}
				});
				artistButton.setAlignmentX(Component.CENTER_ALIGNMENT);
				artistButton.setPreferredSize(new Dimension(10, 50));
				scrollPanel.add(artistButton);
			}				
		}
		
		JButton menu = Components.createButton("Menu", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.goToMenu();
			}
		});
		panelButtons.add(menu);
	}
	
	/**
	 * Search for all the selected artist's tracks.
	 * 
	 * @param artist
	 */
	private void selectArtistButtonAction(Artist artist) {
		final JDialog dialog = Components.createDialog(mainFrame, "Searching Tracks...", "Searching, wait!");
		dialog.setVisible(true);
		
		SwingWorker<ArrayList<Track>, Void> worker = new SwingWorker<ArrayList<Track>, Void>() {
			@Override
			protected ArrayList<Track> doInBackground() throws Exception {
				return searchTracksFunctions.getArtistTracks(artist.getId());
			}
			
			@Override
			protected void done() {
				dialog.setVisible(false);
			}
			
		};
		worker.execute();
		
		ArrayList<Track> tracks;
		try {
			tracks = worker.get();
			Collections.sort(tracks);
			showTracks(tracks);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "An error occurred. Try Again", "Error", JOptionPane.ERROR_MESSAGE);
		}	
	}

	/**
	 * Show in the screen all the tracks found.
	 * 
	 * @param tracks
	 */
	private void showTracks(ArrayList<Track> tracks) {
		if(tracks == null) 
			throw new NullPointerException();
		
		mainPanel.removeAll();
		Components.panelRefresh(mainPanel);
		panelButtons.removeAll();
		Components.panelRefresh(panelButtons);
		
		if(tracks.size() == 0) {
			Components.showMessage(mainPanel, "Nothing found!");
		} else {
			JLabel label = Components.createLabel(tracks.size() + " Tracks Found", Color.white, 
					new Font("arial",Font.BOLD, 20), Component.CENTER_ALIGNMENT);
			mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			mainPanel.add(label);
			mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			
			JPanel scrollPanel = new JPanel();
			scrollPanel.setBackground(Colors.GREEN);
			scrollPanel.setLayout(new GridLayout(tracks.size(), 1));
			JScrollPane scroller = Components.createScroller(scrollPanel);
			mainPanel.add(scroller);
			
			for(int i = 0; i < tracks.size(); i++) {
				Track track = tracks.get(i);
				JButton artistButton = Components.createButton(track.getName() + " (" + track.getPopularity() + ")", new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						Functions.browserRedirect("https://open.spotify.com/track/"+track.getId());
					}
				});
				artistButton.setAlignmentX(Component.CENTER_ALIGNMENT);
				artistButton.setPreferredSize(new Dimension(10, 50));
				scrollPanel.add(artistButton);
			}				
		}
		
		JButton menu = Components.createButton("Menu", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.goToMenu();
			}
		});
		panelButtons.add(menu);
		
	}
}
