package unibo.progettotesi.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import unibo.progettotesi.json.planner.Response;
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
		return startPlace.savingString() + "†" + endPlace.savingString() + "†" + startTime.savingString() + "†" + endTime.savingString() + "†" + startStop.savingString() + "†" + endStop.savingString() + savingLegs();
	}

	private String savingLegs() {
		String s = "";
		for (int i = 0; i < legs.size(); i++) {
			s += "†";
			s += legs.get(i).savingString();
		}
		return s;
	}

	public static Route getRouteFromString(String saved){
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "†");
		Route r = new Route(Place.getPlaceFromString(stringTokenizer.nextToken()), Place.getPlaceFromString(stringTokenizer.nextToken()), Time.getTimeFromString(stringTokenizer.nextToken()), Time.getTimeFromString(stringTokenizer.nextToken()), Stop.getStopFromString(stringTokenizer.nextToken()), Stop.getStopFromString(stringTokenizer.nextToken()));
		if(stringTokenizer.hasMoreTokens())
			r.setLegs(getLegsFromString(stringTokenizer.nextToken("«")));
		return r;
	}

	private static List<Leg> getLegsFromString(String s) {
		List<Leg> ll = new ArrayList<Leg>();
		StringTokenizer stringTokenizer = new StringTokenizer(s, "†");
		while(stringTokenizer.hasMoreTokens()){
			ll.add(Leg.getLegFromString(stringTokenizer.nextToken()));
		}
		return ll;
	}

	public static Route getRouteFromPlanner(Response response){
		try{
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(response.getStartime());
			Time startTime = new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

			calendar.setTimeInMillis(response.getEndtime());
			Time endTime = new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

			Location start = new Location(Double.parseDouble(response.getFrom().getLat()), Double.parseDouble(response.getFrom().getLon()), response.getFrom().getName());
			Location end = new Location(Double.parseDouble(response.getTo().getLat()), Double.parseDouble(response.getTo().getLon()), response.getTo().getName());
			Place startPlace = new Place(start.getAddress(), start);
			Place endPlace = new Place(end.getAddress(), end);

			List<Leg> legs = new ArrayList<Leg>();

			for (int i = 0; i < response.getLeg().size(); i++) {
				unibo.progettotesi.json.planner.Leg legR = response.getLeg().get(i);
				String transportType = legR.getTransport().getType();

				if(transportType != null && !transportType.equals("") && !transportType.equals("WALK")){
					Location startL = new Location(Double.parseDouble(legR.getFrom().getLat()), Double.parseDouble(legR.getFrom().getLon()), legR.getFrom().getName());
					StringTokenizer stringTokenizer = new StringTokenizer(legR.getFrom().getStopId().getId(), "_");
					Stop startSt = new Stop(startL, Integer.parseInt(stringTokenizer.nextToken()), legR.getFrom().getName());
					Location endL = new Location(Double.parseDouble(legR.getTo().getLat()), Double.parseDouble(legR.getTo().getLon()), legR.getTo().getName());
					stringTokenizer = new StringTokenizer(legR.getTo().getStopId().getId(), "_");
					Stop endSt = new Stop(endL, Integer.parseInt(stringTokenizer.nextToken()), legR.getTo().getName());
					Line line = new Line(legR.getTransport().getRouteShortName(), legR.getTransport().getTripId());
					stringTokenizer = new StringTokenizer(legR.getTransport().getRouteId(), "_");
					stringTokenizer.nextToken();
					stringTokenizer.nextToken();
					int direction = Integer.parseInt(stringTokenizer.nextToken());
					calendar.setTimeInMillis(legR.getStartime());
					Time departure = new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
					calendar.setTimeInMillis(legR.getEndtime());
					Time arrival = new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
					legs.add(new Leg(startSt, endSt, line, departure, arrival, direction));
				}

			}

			return new Route(legs, startPlace, endPlace, startTime, endTime, legs.get(0).getStartStop(), legs.get(legs.size() - 1).getEndStop());
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
