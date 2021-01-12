package utils;

/**
 * Represent an Artist. The artist has name, id and popularity.
 * 
 * @author Pedro Nóbrega
 *
 */
public class Artist {
	
	private String name;
	private String id;
	private int popularity;
	
	/**
	 * Construct an aritst.
	 * 
	 * @param name
	 * @param id
	 * @param popularity
	 */
	public Artist(String name, String id, int popularity) {
		this.name = name;
		this.id = id;
		this.popularity = popularity;
	}
	
	/**
	 * Get the artist name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the aritst id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Get the artist popularity.
	 * 
	 * @return the popularity
	 */
	public int getPopularity() {
		return this.popularity;
	}
	
	/**
	 * Return a string that represent the artist.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		return this.id + " - " + this.name + " - " + this.popularity;
	}
	
	/**
	 * Return the artist HashCode.
	 * 
	 * @return the HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Compare if an artist is equal to another by the id.
	 * 
	 * @return a true if the artists are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
