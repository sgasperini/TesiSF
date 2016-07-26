package unibo.progettotesi.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import unibo.progettotesi.utilities.Time;

public class Leg {
	private Stop startStop;
	private Stop endStop;
	private Line line;
	private Time startTime;
	private Time endTime;
	private List<Stop> interStops;

	public Leg(Stop startStop, Stop endStop, Line line, Time startTime, Time endTime, List<Stop> interStops) {
		this.startStop = startStop;
		this.endStop = endStop;
		this.line = line;
		this.startTime = startTime;
		this.endTime = endTime;
		this.interStops = interStops;
	}

	public Leg(Stop startStop, Stop endStop, Line line, Time startTime, Time endTime) {
		this.startStop = startStop;
		this.endStop = endStop;
		this.line = line;
		this.startTime = startTime;
		this.endTime = endTime;
		interStops = new ArrayList<Stop>();
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

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
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

	public List<Stop> getInterStops() {
		return interStops;
	}

	public void setInterStops(List<Stop> interStops) {
		this.interStops = interStops;
	}

	public void addInterStops(Stop newStop) {
		interStops.add(newStop);
	}

	public String savingString() {
		return startStop.savingString() + "¬" + endStop.savingString() + "¬" + line.savingString() + "¬" + startTime.savingString() + "¬" + endTime.savingString();
	}

	public static Leg getLegFromString(String saved) {
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "¬");
		return new Leg(Stop.getStopFromString(stringTokenizer.nextToken()), Stop.getStopFromString(stringTokenizer.nextToken()), Line.getLineFromString(stringTokenizer.nextToken()), Time.getTimeFromString(stringTokenizer.nextToken()), Time.getTimeFromString(stringTokenizer.nextToken()));
	}
}
