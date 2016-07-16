package unibo.progettotesi.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import unibo.progettotesi.R;
import unibo.progettotesi.activities.SelectRouteActivityB;
import unibo.progettotesi.model.Leg;
import unibo.progettotesi.model.Route;

public class Filler {

	public static void fillRoute(View view, final Route route, final Activity activity){
		TextView times = (TextView) view.findViewById(R.id.times_route);
		TextView duration = (TextView) view.findViewById(R.id.duration_route);
		TextView startStop = (TextView) view.findViewById(R.id.startStop_route);
		TextView endStop = (TextView) view.findViewById(R.id.endStop_route);
		TextView lines = (TextView) view.findViewById(R.id.lines_route);

		//RelativeLayout relativeLayout = (RelativeLayout) view/*.findViewById(R.id.layout_route)*/;

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectRouteActivityB.selectRoute(activity, route);
			}
		});

		times.setText(route.getStartTime() + " - " + route.getEndTime());
		duration.setText(Time.getDifference(route.getStartTime(), route.getEndTime()) + " min");
		startStop.setText(route.getStartStop().getName());
		endStop.setText(route.getEndStop().getName());
		lines.setText(route.getLines());
	}

	public static void fillLeg(View view, Leg leg){
		TextView distance = (TextView) view.findViewById(R.id.distance_leg);
		TextView startStop = (TextView) view.findViewById(R.id.startStop_leg);
		TextView startTime = (TextView) view.findViewById(R.id.busStartRealTime_leg);
		TextView line = (TextView) view.findViewById(R.id.line_leg);
		TextView stops = (TextView) view.findViewById(R.id.stops_leg);
		TextView endStop = (TextView) view.findViewById(R.id.endStop_leg);
		TextView endTime = (TextView) view.findViewById(R.id.busEndRealTime_leg);

		RealTimeTracker.setDistanceTo(distance, leg.getStartStop());
		startStop.setText(leg.getStartStop().getName());
		RealTimeTracker.setBusTime(startTime, leg.getStartStop(), leg.getLine(), leg.getStartTime());
		line.setText(leg.getLine().getName());
		stops.setText(leg.getInterStops().size() + " fermate");
		endStop.setText(leg.getEndStop().getName());
		RealTimeTracker.setBusTime(endTime, leg.getEndStop(), leg.getLine(), leg.getEndTime());
	}
}
