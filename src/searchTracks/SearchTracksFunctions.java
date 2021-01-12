package searchTracks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Album;
import utils.Artist;
import utils.Track;

/**
 * Class with all the search tracks functions.
 * 
 * @author Pedro Nóbrega
 *
 */
public class SearchTracksFunctions {

	private RequestSearchTracks request;
	
	/**
	 * Construct the SearchTracksFunctions class.
	 */
	public SearchTracksFunctions() {
		this.request = new RequestSearchTracks();
	}
	
	/**
	 * Search for all the artists that match with the string artistName.
	 * 
	 * @param artistName
	 * @return the ArrayList of Artists
	 */
	public ArrayList<Artist> searchArtists(String artistName) {
		try {
			JSONArray artistsJSON = request.searchArtists(artistName);
			ArrayList<Artist> artists = new ArrayList<>();
			for(int i = 0; i < artistsJSON.length(); i++) {
				JSONObject artist = artistsJSON.getJSONObject(i);
				String id = artist.get("id").toString();
				String name = artist.get("name").toString();
				int popularity = (int)artist.get("popularity");
				artists.add(new Artist(name, id, popularity));
			}
			return artists;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Get all the tracks of an artist.
	 * 
	 * @param id
	 */
	public ArrayList<Track> getArtistTracks(String artistId) {
		try {
			ArrayList<Album> albums = getArtistAlbuns(artistId);
			HashSet<String> tracksIds = request.getAlbumsTracks(albums, artistId);
			ArrayList<Track> tracks = request.getTracksFromId(tracksIds);
			return tracks;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Get all the albums of an artist.
	 * 
	 * @param idArtist
	 * @return the albums list
	 * @throws IOException
	 */
	public ArrayList<Album> getArtistAlbuns(String idArtist) throws IOException {
		JSONArray albunsJSON = request.getAlbums(idArtist);
		ArrayList<Album> albuns = new ArrayList<>();
		for(int i = 0; i < albunsJSON.length(); i++) {
			JSONObject album = albunsJSON.getJSONObject(i);
			String name = album.get("name").toString();
			String idAlbum = album.get("id").toString();
			albuns.add(new Album(name, idAlbum));
		}
		return albuns;
	}

}
