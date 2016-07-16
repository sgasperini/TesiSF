package unibo.progettotesi.utilities;

import android.widget.TextView;

import unibo.progettotesi.model.Line;
import unibo.progettotesi.model.Stop;

public class RealTimeTracker {

	public static void setDistanceTo(TextView textView, Stop stop){
		textView.setText(500 + "");
	}

	public static void setBusTime(TextView textView, Stop stop, Line line, Time time){
		textView.setText(new Time(10, 30).toString());
	}

}
