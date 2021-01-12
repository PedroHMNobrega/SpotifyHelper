package backup;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.Functions;

/**
 * RequestBackup is used to make HTTP requests to Spotify API.
 * 
 * @author Pedro Nóbrega
 *
 */
public class RequestBackup {
	/**
	 * User access token, used to authentication. 
	 */
	private String accessToken;
	private boolean test = false;

	/**
	 * Construct a Request instance.
	 * 
	 * @param accessToken
	 */
	public RequestBackup(String accessToken) {
		this.accessToken = accessToken;
	}
	
	/**
	 * Get the user's Spotify saved tracks.
	 * 
	 * @return the user saved tracks
	 * @throws Exception
	 */
	public JSONArray getTracksRaw() throws Exception {
		String requestUrl = "https://api.spotify.com/v1/me/tracks?limit=50&offset=0";
		return getTracksRaw(requestUrl, new JSONArray());
	}
	
	/**
	 * Recursive function that get the user's Spotify saved tracks page by page.
	 * 
	 * @param requestUrl
	 * @param tracks
	 * @return the user saved tracks
	 * @throws Exception
	 */
	private JSONArray getTracksRaw(String requestUrl, JSONArray tracks) throws Exception {
		URL url = new URL(requestUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "Bearer " + this.accessToken);
		connection.setRequestMethod("GET");
		
		if(connection.getResponseCode() != 200) 
			throw new RuntimeException("Error " + connection.getResponseCode());
		
		String jsonText = Functions.responseToString(connection);		
		connection.disconnect();
		
		JSONObject json = new JSONObject(jsonText);
		JSONArray items = json.getJSONArray("items");
		for(int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			tracks.put(item.get("track"));			
		}
		
		if(!(json.get("next").toString()).equals("null") && !test) 
			this.getTracksRaw(json.get("next").toString(), tracks);
		
		return tracks;
	}
}
