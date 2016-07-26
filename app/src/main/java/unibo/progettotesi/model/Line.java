package unibo.progettotesi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Line {
	private String name;
	private String lastStop;
	private String tripID;
	private List<Stop> stops;

	public Line(String name, String tripID) {
		this.name = name;
		this.tripID = tripID;
	}

	public String getTripID() {
		return tripID;
	}

	public void setTripID(String tripID) {
		this.tripID = tripID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastStop() {
		return lastStop;
	}

	public void setLastStop(String lastStop) {
		this.lastStop = lastStop;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}

	public void addStop(Stop stop){
		stops.add(stop);
	}

	public String savingStringAllStops() {
		return name + "•" + tripID + savingStops();
	}

	public String savingString() {
		return name + "•" + tripID;
	}

	public static Line getLineFromString(String saved){
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "•");
		Line l = new Line(stringTokenizer.nextToken(), stringTokenizer.nextToken());
		if(stringTokenizer.hasMoreTokens())
			l.setStops(getStopsFromString(stringTokenizer.nextToken("«")));
		return l;
	}

	private static List<Stop> getStopsFromString(String s) {
		List<Stop> ls = new ArrayList<Stop>();
		StringTokenizer stringTokenizer = new StringTokenizer(s, "•");
		while(stringTokenizer.hasMoreTokens()){
			ls.add(Stop.getStopFromString(stringTokenizer.nextToken()));
		}
		return ls;
	}

	private String savingStops() {
		String s = "";
		for (int i = 0; i < stops.size(); i++) {
			if(i > 0)
				s += "•";
			s += stops.get(i).savingString();
		}
		return s;
	}
}
