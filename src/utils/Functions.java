package utils;

import java.awt.Desktop;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;

public class Functions {
	/**
	 * Get a image from its path.
	 * 
	 * @param src
	 * @return
	 */
	public Image getIconImage(String src) {
		Image img = null;
		try {
			img = ImageIO.read(getClass().getResource(src));
		} catch(IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	/**
	 * Converts the request response to a String.
	 * 
	 * @param connection
	 * @return the response string
	 * @throws IOException
	 */
	public static String responseToString(HttpURLConnection connection) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
		String output;
		StringBuffer response = new StringBuffer();
		while((output = br.readLine()) != null) response.append(output); 
		return response.toString();
	}
	
	/**
	 * Open the user's browser and redirect to the URL.
	 */
	public static void browserRedirect(String url) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
		    try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Generate all the requests URL for the tracks. Based on the limit of 50 tracks per URL.
	 * 
	 * @param tracksIds
	 * @param url
	 * @return the URL list
	 */
	public static ArrayList<String> getUrlList(HashSet<String> tracksIds, String url) {
		int limit = 50;
		ArrayList<String> urlList = new ArrayList<>();
		int cnt = 0;
		String ids = "";
		for(String id : tracksIds) {
			if(cnt == limit) {
				urlList.add(url+"?ids=" + ids);
				cnt = 0;
				ids = "";
			} 
			if(!ids.equals("")) ids += ",";
			ids += id;
			cnt++;
		}
		if(cnt > 0) urlList.add(url+"?ids=" + ids);
		return urlList;
	}
}
