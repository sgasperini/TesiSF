package unibo.progettotesi.model;

import android.location.Geocoder;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.location.Address;

public class Location {
	private String address;
	private double latitude;
	private double longitude;


	public Location(double latitude, double longitude, String address) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = " ";
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String savingString(){
		return latitude + "ø" + longitude + "ø" + address;
	}

	public static Location getLocationFromString(String saved) {
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "ø");
		double lat = Double.parseDouble(stringTokenizer.nextToken());
		double lon = Double.parseDouble(stringTokenizer.nextToken());
		if(stringTokenizer.hasMoreTokens())
			return new Location(lat, lon, stringTokenizer.nextToken());
		else
			return new Location(lat, lon);
	}
}
