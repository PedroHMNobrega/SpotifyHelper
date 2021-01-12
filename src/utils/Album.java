package utils;

/**
 * Represent an Album.
 * 
 * @author Pedro Nóbrega
 *
 */
public class Album {
	private String name;
	private String id;
	
	/**
	 * Construct an album.
	 * 
	 * @param name
	 * @param id
	 */
	public Album(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	/**
	 * Get the album name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the album id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Return a string representation of an album.
	 * 
	 * @return the string representation
	 */
	@Override
	public String toString() {
		return name + " - " + id;
	}

	/**
	 * Get the album hashCode based on the id.
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
	 * Compare if 2 albums are equal, based on the id.
	 * 
	 * @param obj
	 * @return true if equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
