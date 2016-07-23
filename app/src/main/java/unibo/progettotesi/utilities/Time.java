package unibo.progettotesi.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class Time {
	private int hour;
	private int minute;
	private String hourS;
	private String minuteS;

	public Time(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
		this.hourS = createHourS();
		this.minuteS = createMinuteS();
	}

	public String toString(){
		return hourS + ":" + minuteS;
	}

	public String toString12(){
		return (hour > 12 ? hour - 12 : (hour == 0 ? 12 :hourS)) + ":" + minuteS + (hour >= 12 ? "PM" : "AM");
	}


	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public String getHourS() {
		return hourS;
	}

	public void setHourS(String hourS) {
		this.hourS = hourS;
	}

	public String getMinuteS() {
		return minuteS;
	}

	public void setMinuteS(String minuteS) {
		this.minuteS = minuteS;
	}

	private String createHourS() {
		return (hour < 10 ? "0" : "") + hour;
	}

	private String createMinuteS() {
		return (minute < 10 ? "0" : "") + minute;
	}

	public static int getDifference(Time startTime, Time endTime) {
		return 0/**/;
	}

	public String savingString() {
		return hour + "™" + minute;
	}

	public static Time getTimeFromString(String saved){
		StringTokenizer stringTokenizer = new StringTokenizer(saved, "™");
		return new Time(Integer.parseInt(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken()));
	}

	public static Time now() {
		Calendar c = Calendar.getInstance();
		Date d = c.getTime();
		return new Time(d.getHours(), d.getMinutes());
	}
}
