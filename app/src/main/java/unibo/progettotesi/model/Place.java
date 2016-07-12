package unibo.progettotesi.model;

import java.util.List;
import java.util.StringTokenizer;

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

	public Place(Location location, int walking) {
		this.walking = walking;
		this.location = location;
	}

	public Place(Location location) {
		this.walking = 5;
		this.location = location;
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

	public int getWalking() {
		return walking;
	}

	public void setWalking(int walking) {
		this.walking = walking;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String savingString(){
		return location.savingString() + "∆" + walking;
	}

	public String savingStringFavorite(){
		return location.savingString() + "∆" + walking + "∆" + name;
	}

	public static Place getPlaceFromString(String saved){
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "∆");
		String uno = stringTokenizer.nextToken();
		return new Place (Location.getLocationFromString(uno), Integer.parseInt(stringTokenizer.nextToken()));
	}

	public static Place getFavoritePlaceFromString(String saved){
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "∆");
		return new Place (Location.getLocationFromString(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken()), stringTokenizer.nextToken());
	}
}
