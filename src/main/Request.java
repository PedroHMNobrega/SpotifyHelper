package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Request is used to make HTTP requests to Spotify API.
 * 
 * @author Pedro Nóbrega
 *
 */
public class Request {
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
	public Request(String accessToken) {
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
			throw new RuntimeException("Falha: Erro " + connection.getResponseCode());
		
		String jsonText = responseToString(connection);		
		connection.disconnect();
		
		JSONObject json = new JSONObject(jsonText);
		JSONArray items = json.getJSONArray("items");
		for(int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			tracks.put(item.get("track"));			
		}
		
		if(json.get("next").toString() != "null" && !test) 
			this.getTracksRaw(json.get("next").toString(), tracks);
		
		return tracks;
	}
	
	/**
	 * Converts the request response to a String.
	 * 
	 * @param connection
	 * @return the response string
	 * @throws IOException
	 */
	private String responseToString(HttpURLConnection connection) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
		String output;
		StringBuffer response = new StringBuffer();
		while((output = br.readLine()) != null) response.append(output); 
		return response.toString();
	}
}
