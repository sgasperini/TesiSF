package unibo.progettotesi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import unibo.progettotesi.json.getNextTripsRequest.Date;
import unibo.progettotesi.utilities.Time;

public class Leg {
	private Stop startStop;
	private Stop endStop;
	private Line line;
	private Time startTime;
	private Time endTime;
	private Date startDate;
	private Date endDate;
	private List<Stop> interStops;
	private int direction;

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

	public Leg(Stop startStop, Stop endStop, Line line, Time startTime, Time endTime, int direction) {
		this.startStop = startStop;
		this.endStop = endStop;
		this.line = line;
		this.startTime = startTime;
		this.endTime = endTime;
		this.direction = direction;
		interStops = new ArrayList<Stop>();
	}

	public Leg(Stop startStop, Stop endStop, Line line, Time startTime, Time endTime, int direction, Date startDate, Date endDate) {
		this.startStop = startStop;
		this.endStop = endStop;
		this.line = line;
		this.startTime = startTime;
		this.endTime = endTime;
		this.direction = direction;
		this.startDate = startDate;
		this.endDate = endDate;
		interStops = new ArrayList<Stop>();
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String savingString() {
		return startStop.savingString() + "¬" + endStop.savingString() + "¬" + line.savingString() + "¬" +
				startTime.savingString() + "¬" + endTime.savingString() + "¬" + direction + "¬" +
				startDate.getMonth() + "¬" + startDate.getYear() + "¬" + startDate.getDay() + "¬" +
				endDate.getMonth() + "¬" + endDate.getYear() + "¬" + endDate.getDay();
	}

	public static Leg getLegFromString(String saved) {
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "¬");
		return new Leg(Stop.getStopFromString(stringTokenizer.nextToken()), Stop.getStopFromString(stringTokenizer.nextToken()),
				Line.getLineFromString(stringTokenizer.nextToken()), Time.getTimeFromString(stringTokenizer.nextToken()),
				Time.getTimeFromString(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken()),
				new Date(Integer.parseInt(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken()),
						Integer.parseInt(stringTokenizer.nextToken())),
				new Date(Integer.parseInt(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken())));
	}
}
