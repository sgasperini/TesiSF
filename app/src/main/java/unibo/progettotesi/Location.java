package unibo.progettotesi;

import java.util.ArrayList;
import java.util.List;

public class Location {
	private String address;
	private double latitude;
	private double longitude;

	public Location(String address) {
		this.address = address;
		addressToCoordinates();
	}

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		coordinatesToAddress();
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

	private void addressToCoordinates() {
		//
	}

	private void coordinatesToAddress() {
		//
	}
}
