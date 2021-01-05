package main;

/**
 * Representation of a track.
 * 
 * @author Pedro Nóbrega
 *
 */
public class Track {
	private String name;
	private String id;
	private String album;
	private String[] artist;
	
	/**
	 * The url to a 30 seconds track preview.
	 */
	private String previewUrl;
	
	/**
	 * Duration of the track in ms;
	 */
	private int duration;
	
	/**
	 * int between 0 and 100
	 */
	private int popularity;
	
	/**
	 * Construct a track.
	 * 
	 * @param name
	 * @param id
	 * @param album
	 * @param artist
	 * @param previewUrl
	 * @param duration
	 * @param popularity
	 */
	public Track(String name, String id, String album, String[] artist, String previewUrl, 
			int duration, int popularity) {
		this.name = removeTabs(name);
		this.id = removeTabs(id);
		this.album = removeTabs(album);
		this.artist = artist;
		this.previewUrl = removeTabs(previewUrl);
		this.duration = duration;
		this.popularity = popularity;
	}
	
	/**
	 * Remove commas from a string, to avoid errors when converting to CSV.
	 * 
	 * @param s
	 * @return the string without commas
	 */
	private String removeTabs(String s) {
		return s.replace("\t", "");
	}
	
	/**
	 * Get the track name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the track id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Get the album name.
	 * 
	 * @return the album
	 */
	public String getAlbum() {
		return this.album;
	}
	
	/**
	 * Get the array of artists.
	 * 
	 * @return the artists
	 */
	public String[] getArtists() {
		return this.artist.clone();
	}
	
	/**
	 * Convert the array of artists to a string.
	 * 
	 * @return string of artists
	 */
	public String artistsToString() {
		String artists = "";
		for(int i = 0; i < this.artist.length; i++) {
			if(i != 0) artists += " | "; 
			artists += removeTabs(this.artist[i]);
		}
		return artists;
	}
	
	/**
	 * Get the track preview URL.
	 * 
	 * @return the previewUrl
	 */
	public String getPreviewUrl() {
		return this.previewUrl;
	}
	
	/**
	 * Get the track duration.
	 * 
	 * @return the track duration
	 */
	public int getDuration() {
		return this.duration;
	}
	
	/**
	 * Convert the track duration to a string with the duration in minutes and seconds.
	 * 
	 * @return the duration in minutes and seconds
	 */
	public String getDurationMinutes() {
		int seconds = this.duration / 1000;
		int minutes = seconds/60;
		seconds -= minutes * 60;
		return minutes + " Min " + seconds + " Sec";
	}
	
	/**
	 * Get the track popularity.
	 * 
	 * @return the track popularity
	 */
	public int getPopularity() {
		return this.popularity;
	}
	
	/**
	 * Get an array of all the attributes of a track to convert to CSV.
	 * 
	 * @return the String array
	 */
	public String toCSV() {
		return this.name + "\t" + this.album + "\t" + this.artistsToString() + "\t" + this.previewUrl + 
				"\t" + this.duration + "\t" + this.popularity + "\t" + this.id;
	}

	/**
	 * Get a string representation of the track.
	 * 
	 * @return the string representation of the track
	 */
	@Override
	public String toString() {
		return this.getName() + " - " + this.artistsToString() + " - " + getDurationMinutes();
	}

	/**
	 * Get the track hashCode based on the track id.
	 * 
	 * @return the hashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Compare if the tracks are equal based on the id.
	 * 
	 * @return if the tracks are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Track other = (Track) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
