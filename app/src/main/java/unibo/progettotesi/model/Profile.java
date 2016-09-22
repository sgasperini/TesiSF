package unibo.progettotesi.model;

import android.content.Context;
import android.location.*;

import java.util.StringTokenizer;

import unibo.progettotesi.utilities.LocationToolbox;

public class Profile implements Comparable{
	private Place start;
	private Place end;
	private String name;
	private Integer distance;

	public Profile(Place start, Place end, String name) {
		this.start = start;
		this.end = end;
		this.name = name;
	}

	public Place getStart() {
		return start;
	}

	public void setStart(Place start) {
		this.start = start;
	}

	public Place getEnd() {
		return end;
	}

	public void setEnd(Place end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String savingString(){
		return start.savingString() + "∫" + end.savingString() + "∫" + name;
	}

	public static Profile getProfileFromString(String saved){
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "∫");
		return new Profile (Place.getPlaceFromString(stringTokenizer.nextToken()), Place.getPlaceFromString(stringTokenizer.nextToken()), stringTokenizer.nextToken());
	}


	@Override
	public int compareTo(Object otherObj) {
		Profile other = (Profile) otherObj;

		return distance.compareTo(other.distance);
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public Integer getDistance() {
		return distance;
	}
}
