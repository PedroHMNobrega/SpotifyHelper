package main;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrackTest {

	private Track track1, track2, track3;
	
	@BeforeEach
	void setUp() throws Exception {
		this.track1 = new Track("Test Music1", "123", "The album", new String[] {"a1", "a2"}, "www.test.com", 10000, 100);
		this.track2 = new Track("Music2", "234", "No Name", new String[] {"The artist"}, "www.music2.com", 156000, 50);
		this.track3 = new Track("Music2", "123", "No Name", new String[] {"The artist"}, "www.music2.com", 156000, 50);
	}

	@Test
	void testToString() {
		String expectedString1 = "Test Music1 - a1 | a2 - 0 Min 10 Sec";
		String expectedString2 = "Music2 - The artist - 2 Min 36 Sec";
		
		assertEquals(expectedString1, track1.toString());
		assertEquals(expectedString2, track2.toString());
	}

	@Test
	void testHashCodeEquals() {
		assertEquals(track1.hashCode(), track3.hashCode());
		assertNotEquals(track2.hashCode(), track3.hashCode());
		
		assertTrue(track1.equals(track3));
		assertFalse(track2.equals(track3));
	}
	
	@Test
	void testToCsv() {
		String expectedString = "Test Music1\tThe album\ta1 | a2\twww.test.com\t10000\t100\t123";
		assertEquals(expectedString, track1.toCSV());
	}
}
