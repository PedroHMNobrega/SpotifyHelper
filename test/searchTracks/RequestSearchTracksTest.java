package searchTracks;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import utils.Album;

class RequestSearchTracksTest {

	private RequestSearchTracks request;
	
	@BeforeEach
	void setUp() throws Exception {
		this.request = new RequestSearchTracks();
		this.request.getToken();
	}

	@Test
	void testGetAlbumsTracks() throws Exception {
		Album album1 = new Album("Teste1", "7en2oNzOC5VsvrscsKhBsQ");
		Album album2 = new Album("Teste2", "5I6tcREZBLHpFh1qyqZlB0");
		ArrayList<Album> albums = new ArrayList<>();
		albums.add(album1);
		albums.add(album2);
		HashSet<String> tracks = request.getAlbumsTracks(albums, "2b1d97CxeIjz2phOfRVPpd");
		assertEquals(398, tracks.size());
	}

}
