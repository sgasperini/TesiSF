package unibo.progettotesi.model;

import java.util.List;

public class Line {
	private String name;
	private String lastStop;
	private List<Stop> stops;

	public Line(String name, String lastStop) {
		this.name = name;
		this.lastStop = lastStop;
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
}
