package unibo.progettotesi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import unibo.progettotesi.R;
import unibo.progettotesi.activities.FavoritesProfileB;
import unibo.progettotesi.activities.NewTripActivityB;
import unibo.progettotesi.activities.SelectRouteActivityB;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.Filler;
import unibo.progettotesi.utilities.Time;


public class RoutesAdapter extends ArrayAdapter<Route> {
	private int resource;
	private SelectRouteActivityB selectRouteActivityB;

	public RoutesAdapter(SelectRouteActivityB selectRouteActivityB, int _resource, List<Route> items) {
		super((Context) selectRouteActivityB, _resource, items);
		resource = _resource;
		this.selectRouteActivityB = selectRouteActivityB;
	}

	@Override
	public View getView(int position, final View convertView, ViewGroup parent) {
		final LinearLayout newView;
		final int pos = position;

		final Route route = getItem(position);

		if (convertView == null) {
			newView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, newView, true);
		} else {
			newView = (LinearLayout) convertView;
		}

		Filler.fillRoute(newView, route, selectRouteActivityB);

		return newView;
	}
}

