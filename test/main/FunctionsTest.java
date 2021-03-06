package main;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backup.BackupFunctions;
import utils.Track;

class FunctionsTest {
	
	private BackupFunctions ac;

	@BeforeEach
	void setUp() throws Exception {
		this.ac = new BackupFunctions();
	}

	@Test
	void trackListToCSVTest() {
		Track track1 = new Track("Test Music1", "123", "The album", new String[] {"a1", "a2"}, "www.test.com", 10000, 100, "wwwcg");
		Track track2 = new Track("Music2", "234", "No Name", new String[] {"The artist"}, "www.music2.com", 156000, 50, "www2");
		Track track3 = new Track("Music3", "1gfdgsd", "No Name df", new String[] {"The artist", "fsddf"}, "www.music2ssfdf.com", 15000, 59, "www3");
		ArrayList<Track> tracks = new ArrayList<>();
		tracks.add(track1);
		tracks.add(track2);
		tracks.add(track3);
		
		String expectedString = "Name\tAlbum\tArtists\tPreview URL\tURI\tDuration(ms)\tPopularity\tId\n"+
				track1.toCSV() + "\n" + track2.toCSV() + "\n" + track3.toCSV() + "\n";
		assertEquals(expectedString, ac.trackListToCSV(tracks));
	}

}
