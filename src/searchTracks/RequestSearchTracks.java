package searchTracks;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Album;
import utils.Config;
import utils.Functions;
import utils.Track;

/**
 * RequestSearchTracks is used to make HTTP requests to Spotify API.
 * 
 * @author Pedro Nóbrega
 *
 */
public class RequestSearchTracks {
	
	private String token = null;
	
	/**
	 * Get the authentication token to access the spotify API.
	 * 
	 * @return the auth token
	 * @throws IOException
	 */
	public String getToken() throws IOException {
		if(token != null) 
			return token;
		
		String credentials = Config.CLIENT_ID + ":" + Config.CLIENT_SECRET;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
		
		URL url = new URL("https://accounts.spotify.com/api/token");
		String urlParameters = "grant_type=client_credentials";
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Authorization", "Basic " + encodedCredentials);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		Writer wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(urlParameters);
		wr.flush();
		wr.close();
		
		if(conn.getResponseCode() != 200)
			throw new RuntimeException("Error " + conn.getResponseCode());
		
		String jsonText = Functions.responseToString(conn);
		conn.disconnect();
		
		JSONObject json = new JSONObject(jsonText);
		token = json.getString("access_token");
		return token;
	}
	
	/**
	 * Get all the artists that match with the artistName.
	 * 
	 * @param artistName
	 * @return the artists JSONArray
	 * @throws IOException
	 */
	public JSONArray searchArtists(String artistName) throws IOException {
		String artistNameEncoded = URLEncoder.encode(artistName, StandardCharsets.UTF_8.toString());
		String requestUrl = "https://api.spotify.com/v1/search?type=artist&limit=50&offset=0&q=" + artistNameEncoded;
		String token = this.getToken();
		return searchArtists(requestUrl, token, new JSONArray());
	}

	/**
	 * Recursive function that get the artists found, page by page.
	 * 
	 * @param requestUrl
	 * @param token
	 * @param artists
	 * @return the artists JSONArray
	 * @throws IOException
	 */
	private JSONArray searchArtists(String requestUrl, String token, JSONArray artists) throws IOException {
		JSONObject json = connect(requestUrl);
		JSONArray items = json.getJSONObject("artists").getJSONArray("items");
		for(int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			artists.put(item);
		}
		
		String nextUrl = json.getJSONObject("artists").get("next").toString();
		
		if(!nextUrl.equals("null"))
			this.searchArtists(nextUrl, token, artists);
		
		return artists;
	}

	/**
	 * Get all the artist's albums.
	 * 
	 * @param id
	 * @return the JSONArray with the artist's albums
	 * @throws IOException
	 */
	public JSONArray getAlbums(String id) throws IOException {
		String url = "https://api.spotify.com/v1/artists/" + id + "/albums?limit=50&offset=0";
		return getAlbums(url, new JSONArray());
	}
	
	/**
	 * Recursive function that get all the artist's albums, page by page.
	 * 
	 * @param requestUrl
	 * @param albuns
	 * @return the JSONArray with the artist's albums
	 * @throws IOException
	 */
	private JSONArray getAlbums(String requestUrl, JSONArray albuns) throws IOException {
		JSONObject json = connect(requestUrl);
		JSONArray items = json.getJSONArray("items");
		for(int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			albuns.put(item);
		}
		
		String nextUrl = json.get("next").toString();
		
		if(!nextUrl.equals("null"))
			this.getAlbums(nextUrl, albuns);
		
		return albuns;
	}

	/**
	 * Get the tracks ids of all the albums.
	 * 
	 * @param albuns
	 * @param artistId
	 * @return the hashSet with the tracks ids
	 * @throws Exception
	 */
	public HashSet<String> getAlbumsTracks(ArrayList<Album> albuns, String artistId) throws Exception {
		String requestUrl = "https://api.spotify.com/v1/albums";
		HashSet<String> tracksIds = new HashSet<>();
		
		for(int i = 0; i < albuns.size(); i += 20) {
			String ids = "";
			for(int j = 0; j < 20; j++) {
				if(i+j >= albuns.size()) break;
				if(j != 0) ids += ",";
				ids += albuns.get(i + j).getId();
			}
			
			JSONObject json = connect(requestUrl + "?ids=" + ids);
			JSONArray albumsJSON = json.getJSONArray("albums"); 
			for(int k = 0; k < albumsJSON.length(); k++) {
				JSONObject tracksJSON = albumsJSON.getJSONObject(k).getJSONObject("tracks");
				tracksIds.addAll(getAlbumTracks(tracksJSON, new HashSet<>(), artistId));
			}
		}
		return tracksIds;
	}
	
	/**
	 * Get an album's tracks ids.
	 * 
	 * @param tracksJSON
	 * @param tracks
	 * @param artistId
	 * @return the hashSet with the tracks ids
	 * @throws Exception
	 */
	private HashSet<String> getAlbumTracks(JSONObject tracksJSON, HashSet<String> tracks, String artistId) throws Exception {	
		JSONArray items = tracksJSON.getJSONArray("items");
		for(int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			String trackId = item.get("id").toString();
			if(verifyTrack(item.getJSONArray("artists"), artistId))
				tracks.add(trackId);
		}
		
		String nextUrl = tracksJSON.get("next").toString();
		
		if(!nextUrl.equals("null"))
			tracks.addAll(this.getTracksIds(nextUrl, new HashSet<>(), artistId));
		
		return tracks;
	}
	
	/**
	 * Get the ids of the tracks in a spotify track object.
	 * 
	 * @param requestUrl
	 * @param tracks
	 * @param artistId
	 * @return the hashSet with the tracks ids
	 * @throws Exception
	 */
	private HashSet<String> getTracksIds(String requestUrl, HashSet<String> tracks, String artistId) throws Exception {
		JSONObject json = connect(requestUrl);
		JSONArray items = json.getJSONArray("items");
		for(int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			String trackId = item.get("id").toString();
			if(verifyTrack(item.getJSONArray("artists"), artistId))
				tracks.add(trackId);
		}
		
		String nextUrl = json.get("next").toString();
		
		if(!nextUrl.equals("null"))
			this.getTracksIds(nextUrl, tracks, artistId);
		
		return tracks;
	}
	
	/**
	 * Verify if the track belongs to the artist selected by the user.
	 * 
	 * @param artists
	 * @param artistId
	 * @return true if a track belongs to the artist selected
	 */
	private boolean verifyTrack(JSONArray artists, String artistId) {
		for(int i = 0; i < artists.length(); i++) {
			JSONObject artist = artists.getJSONObject(i);
			if(artistId.equals(artist.get("id").toString()))
				return true;
		}
		return false;
	}

	/**
	 * Get tracks by the id.
	 * 
	 * @param tracksIds
	 * @return the arrayList of tracks
	 * @throws Exception
	 */
	public ArrayList<Track> getTracksFromId(HashSet<String> tracksIds) throws Exception {
		ArrayList<String> urlList = Functions.getUrlList(tracksIds, "https://api.spotify.com/v1/tracks");
		ArrayList<Track> tracks = new ArrayList<>();
		for(String requestUrl: urlList) {
			JSONObject json = connect(requestUrl);
			JSONArray tracksJSON = json.getJSONArray("tracks");
			for(int i = 0; i < tracksJSON.length(); i++) {
				JSONObject trackJSON = tracksJSON.getJSONObject(i);
				String id = trackJSON.get("id").toString();
				String name = trackJSON.get("name").toString();
				String uri = trackJSON.get("uri").toString();
				int popularity = (int)trackJSON.get("popularity");
				tracks.add(new Track(name, id, "", null, "", 0, popularity, uri));
			}
		}
		return tracks;
	}
	
	/**
	 * Send a GET request to the Spotify API.
	 * 
	 * @param requestUrl
	 * @return the JSONObject with the response
	 * @throws IOException
	 */
	private JSONObject connect(String requestUrl) throws IOException {
		URL url = new URL(requestUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bearer " + this.getToken());
		
		if(conn.getResponseCode() != 200)
			throw new RuntimeException("Error " + conn.getResponseCode());
		
		String jsonText = Functions.responseToString(conn);
		conn.disconnect();
		
		return new JSONObject(jsonText);
	}
}
