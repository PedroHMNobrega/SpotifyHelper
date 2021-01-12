package backup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.Track;

/**
 * Class with all the backup functions.
 * 
 * @author Pedro Nóbrega
 *
 */
public class BackupFunctions {
	
	/**
	 * Get an array with all the user's Spotify saved tracks.
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Track> getTrackList(String accessToken) throws Exception {
		RequestBackup request = new RequestBackup(accessToken);
		JSONArray tracks = request.getTracksRaw();
		ArrayList<Track> trackList = new ArrayList<>();
		
		for(int i = 0; i < tracks.length(); i++) {
			JSONObject track = tracks.getJSONObject(i);
			String name = track.get("name").toString();
			String id = track.get("id").toString();
			String album = track.getJSONObject("album").get("name").toString();
			String[] artists = getArtists(track.getJSONArray("artists"));
			String previewUrl = track.get("preview_url").toString();
			int duration = (int)track.get("duration_ms");
			int popularity = (int)track.get("popularity");
			String uri = track.get("uri").toString();
			
			Track music = new Track(name, id, album, artists, previewUrl, duration, popularity, uri);
			trackList.add(music);
		}
		
		return trackList;
	}
	
	/**
	 * Converts a JSONArray to a String array.
	 * 
	 * @param artistList
	 * @return the string array.
	 */
	private String[] getArtists(JSONArray artistList) {
		String[] artists = new String[artistList.length()];
		for(int i = 0; i < artistList.length(); i++) {
			JSONObject artist = artistList.getJSONObject(i);
			artists[i] = artist.getString("name");
		}
		return artists;
	}
	
	/**
	 * Save the track list in a CSV file.
	 * 
	 * @param trackList
	 * @return true if the file was saved.
	 */
	public boolean saveFile(JFrame frame, ArrayList<Track> trackList) {
		File file = saveAs(frame);
		if(file != null) {	
			try {
				FileWriter write = new FileWriter(file);				
				write.append(trackListToCSV(trackList));
				write.flush();
				write.close();
				return true;
			} catch(IOException ioe) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Get the file that the user chose.
	 * 
	 * @return the chosen file
	 */
	private File saveAs(JFrame frame) {
		JFileChooser fc = new JFileChooser() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void approveSelection() {				
				File f = new File(fileExtensionToCSV(getSelectedFile().getAbsolutePath()));
				if(f.exists() && getDialogType() == SAVE_DIALOG) {
					int result = JOptionPane.showConfirmDialog(this, "The file already exists, overwrite?", 
							"Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
					if(result == JOptionPane.YES_OPTION) super.approveSelection();
					else if(result == JOptionPane.CANCEL_OPTION) super.cancelSelection();
					return;
				}
				super.approveSelection();
			}
		};
		fc.setDialogTitle("Save Backup");
		
		int userSelection = fc.showSaveDialog(frame);
		if(userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = new File(fileExtensionToCSV(fc.getSelectedFile().getAbsolutePath()));
			return fileToSave;
		} 
		return null;
	}
	
	/**
	 * Set the file extension to CSV.
	 * 
	 * @param path
	 * @return
	 */
	private String fileExtensionToCSV(String path) {
		String ext = getExtension(path);
		if(!ext.equals("csv")) path += ".csv";
		return path;
	}
	
	/**
	 * Convert a track list into a CSV string, separated by tabs.
	 * 
	 * @param trackList
	 * @return the CSV string
	 */
	public String trackListToCSV(ArrayList<Track> trackList) {
		String csv = "Name\tAlbum\tArtists\tPreview URL\tURI\tDuration(ms)\tPopularity\tId\n";
		for(int i = 0; i < trackList.size(); i++) {
			csv += trackList.get(i).toCSV();
			csv += "\n";
		}
		return csv;
	}
	
	/**
	 * Let the user choose a CSV file.
	 * 
	 * @param frame
	 * @return the CSV file.
	 */
	public File getFile(JFrame frame) {
		JFileChooser fc = new JFileChooser() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void approveSelection() {
				File f = getSelectedFile();
				String ext = getExtension(f.getAbsolutePath());
				if(getDialogType() == SAVE_DIALOG && (!ext.equals("csv") || !validFile(f))) {
					JOptionPane.showMessageDialog(null, "Choose a valid file.", "Invalid File", JOptionPane.ERROR_MESSAGE);
					return;
				} 
				super.approveSelection();
			}
		};
		fc.setDialogTitle("Open Backup");
		fc.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
		
		int userSelection = fc.showSaveDialog(frame);
		if(userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;
		} 
		return null;
	}
	
	/**
	 * Get the differences between the current trackList and the backUp trackList chosen by the user.
	 * Returns a HashMap with two keys, "new" and "removed", each key points to an ArrayList with the 
	 * new and removed tracks respectively.
	 * 
	 * @param file
	 * @param curr
	 * @return the HashMap
	 */
	public HashMap<String, ArrayList<Track>> compareBackup(File file, ArrayList<Track> curr) {	
		ArrayList<Track> backUp = this.getTracksFromFile(file);
		if(backUp != null) {
			HashMap<String, ArrayList<Track>> diff = new HashMap<>();
			diff.put("new", new ArrayList<>()); 
			diff.put("removed", new ArrayList<>());
			HashMap<String, Boolean> currIds = new HashMap<>();
			HashMap<String, Boolean> backUpIds = new HashMap<>();
			
			for(int i = 0; i < curr.size(); i++) currIds.put(curr.get(i).getId(), true);
			for(int i = 0; i < backUp.size(); i++) backUpIds.put(backUp.get(i).getId(), true);
			
			for(int i = 0; i < curr.size(); i++) {
				Track currTrack = curr.get(i);
				if(!backUpIds.containsKey(currTrack.getId())) 
					diff.get("new").add(currTrack);	
			}
			for(int i = 0; i < backUp.size(); i++) {
				Track currTrack = backUp.get(i);
				if(!currIds.containsKey(currTrack.getId())) 
					diff.get("removed").add(currTrack);	
			}
			
			return diff;
		}
		return null;
	}
	
	/**
	 * Get a file extension from its path.
	 * 
	 * @param path
	 * @return the file extension
	 */
	private String getExtension(String path) {
		String div[] = path.split("\\.");
		return div[div.length-1];
	}
	
	/**
	 * Verify if the CSV file is valid.
	 * 
	 * @param file
	 * @return true if the file is valid
	 */
	private boolean validFile(File file) {
		try {
			int numberOfColumns = 8;
			
			BufferedReader csvReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			String row;
			int cnt = 0;
			while((row = csvReader.readLine()) != null) {
				String[] data = row.split("\t");
				if(data.length != numberOfColumns) {
					csvReader.close();
					return false;
				}
				cnt++;
			}
			csvReader.close();
			if(cnt < 1) return false;
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * Read the CSV file and convert it to an ArrayList of tracks.
	 * 
	 * @param file
	 * @return the ArrayList of tracks
	 */
	private ArrayList<Track> getTracksFromFile(File file) {
		try {
			ArrayList<Track> tracks = new ArrayList<>();
			BufferedReader csvReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			String row;
			csvReader.readLine();
			while((row = csvReader.readLine()) != null) {
				String[] data = row.split("\t");
				String[] artists = data[2].split("\\|");
				for(int i = 0; i < artists.length; i++) artists[i] = artists[i].trim();
				Track track = new Track(data[0], data[7], data[1], artists, data[3], 
						Integer.parseInt(data[5]), Integer.parseInt(data[6]), data[4]);
				tracks.add(track);
			}
			csvReader.close();
			return tracks;			
		} catch(Exception e) {
			return null;
		}
	}
}
