package unibo.progettotesi.utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import unibo.progettotesi.model.Leg;
import unibo.progettotesi.model.Line;
import unibo.progettotesi.model.Location;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.model.Stop;

public class RouteFinder {
	private Place startPlace;
	private Place endPlace;
	private Time startTime;
	private List<Route> routes;

	public RouteFinder(Place startPlace, Place endPlace, Time startTime) {
		this.startPlace = startPlace;
		this.endPlace = endPlace;
		this.startTime = startTime;
	}

	public RouteFinder(Place startPlace, Place endPlace) {
		this.startPlace = startPlace;
		this.endPlace = endPlace;
		startTime = Time.now();
	}

	public List<Route> calculateRoutes(){	//DA RIVEDERE
		routes = new ArrayList<Route>();
		routes.add(new Route(startPlace, endPlace, startTime, new Time(startTime.getHour(), startTime.getMinute() + 50), new Stop(new Location(44.0, 11.0, "XYZciao"), 24, "fermino", "guarda che non esiste"), new Stop(new Location(44.527465, 11.0, "XYZgrazie"), 31, "fermata", "fittizia anch'essa")));
		routes.add(new Route(startPlace, endPlace, startTime, new Time(startTime.getHour() + 1, startTime.getMinute() + 20), new Stop(new Location(44.000012, 11.0, "XYZciao vicino"), 25, "fermino", "guarda che non esiste di fronte"), new Stop(new Location(44.527465, 11.0, "XYZgrazie"), 31, "fermata", "fittizia anch'essa")));
		routes.get(0).addLeg(new Leg(
				new Stop(new Location(44.0, 11.0, "XYZciao"), 24, "fermino", "guarda che non esiste"),
				new Stop(new Location(44.527465, 11.0, "XYZgrazie"), 31, "fermata", "fittizia anch'essa"),
				new Line("101", "Bologna Autostazione"),
				startTime, new Time(startTime.getHour(), startTime.getMinute() + 50)));
		routes.get(1).addLeg(new Leg(
				new Stop(new Location(44.000012, 11.0, "XYZciao vicino"), 25, "fermino", "guarda che non esiste di fronte"),
				new Stop(new Location(44.527465, 11.0, "XYZgrazie"), 31, "fermata", "fittizia anch'essa"),
				new Line("94", "Bazzano"),
				startTime, new Time(startTime.getHour() + 1, startTime.getMinute() + 20)));
		return routes;
	}
}
