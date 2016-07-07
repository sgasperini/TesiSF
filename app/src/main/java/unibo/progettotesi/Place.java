package unibo.progettotesi;

import java.util.ArrayList;
import java.util.List;

public class Place {
	private Location location;
	private List<Stop> stops;
	private int walking;
	private String name;
	private boolean isFavorite;

	public Place(String name, Location location) {
		this.name = name;
		this.location = location;
		this.walking = 5;	//MINUTI
	}

	public Place(Location location, int walking, String name) {
		this.location = location;
		this.walking = walking;
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}

}
