package unibo.progettotesi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import unibo.progettotesi.utilities.Time;

public class Route {
	private List<Leg> legs;
	private Place startPlace;
	private Place endPlace;
	private Time startTime;
	private Time endTime;
	private Stop startStop;
	private Stop endStop;

	public Route(Place startPlace, Place endPlace, Time startTime, Time endTime, Stop startStop, Stop endStop) {
		this.startPlace = startPlace;
		this.endPlace = endPlace;
		this.startTime = startTime;
		this.endTime = endTime;
		this.startStop = startStop;
		this.endStop = endStop;
		legs = new ArrayList<Leg>();
	}

	public Route(List<Leg> legs, Place startPlace, Place endPlace, Time startTime, Time endTime, Stop startStop, Stop endStop) {
		this.legs = legs;
		this.startPlace = startPlace;
		this.endPlace = endPlace;
		this.startTime = startTime;
		this.endTime = endTime;
		this.startStop = startStop;
		this.endStop = endStop;
	}

	public List<Leg> getLegs() {
		return legs;
	}

	public void setLegs(List<Leg> legs) {
		this.legs = legs;
	}

	public void addLeg(Leg newLeg) {
		legs.add(newLeg);
	}

	public Place getStartPlace() {
		return startPlace;
	}

	public void setStartPlace(Place startPlace) {
		this.startPlace = startPlace;
	}

	public Place getEndPlace() {
		return endPlace;
	}

	public void setEndPlace(Place endPlace) {
		this.endPlace = endPlace;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public Stop getStartStop() {
		return startStop;
	}

	public void setStartStop(Stop startStop) {
		this.startStop = startStop;
	}

	public Stop getEndStop() {
		return endStop;
	}

	public void setEndStop(Stop endStop) {
		this.endStop = endStop;
	}

	public String getLines() {
		String lines = "";
		for (int i = 0; i < legs.size(); i++) {
			if(i > 0)
				lines += ", ";
			lines += legs.get(i).getLine().getName();
		}
		return lines;
	}

	public String savingString(){
		return startPlace.savingString() + "†" + endPlace.savingString() + "†" + startTime.savingString() + "†" + endTime.savingString() + "†" + startStop.savingString() + "†" + endStop.savingString() + "†" + savingLegs();
	}

	private String savingLegs() {
		String s = "";
		for (int i = 0; i < legs.size(); i++) {
			if(i > 0)
				s += "†";
			s += legs.get(i).savingString();
		}
		return s;
	}

	public static Route getRouteFromString(String saved){
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "†");
		Route r = new Route(Place.getPlaceFromString(stringTokenizer.nextToken()), Place.getPlaceFromString(stringTokenizer.nextToken()), Time.getTimeFromString(stringTokenizer.nextToken()), Time.getTimeFromString(stringTokenizer.nextToken()), Stop.getStopFromString(stringTokenizer.nextToken()), Stop.getStopFromString(stringTokenizer.nextToken()));
		if(stringTokenizer.hasMoreTokens())
			r.setLegs(getStopsFromString(stringTokenizer.nextToken("«")));
		return r;
	}

	private static List<Leg> getStopsFromString(String s) {
		List<Leg> ll = new ArrayList<Leg>();
		StringTokenizer stringTokenizer = new StringTokenizer(s, "†");
		while(stringTokenizer.hasMoreTokens()){
			ll.add(Leg.getLegFromString(stringTokenizer.nextToken()));
		}
		return ll;
	}
}
