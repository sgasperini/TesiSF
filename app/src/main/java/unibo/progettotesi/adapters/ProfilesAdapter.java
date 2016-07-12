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
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Profile;


public class ProfilesAdapter extends ArrayAdapter<Profile> {
	private int resource;
	//private FavoritesProfileB favoritesProfileB;
	private boolean start;

	public ProfilesAdapter(NewTripActivityB newTripActivityB, int _resource, List<Profile> items) {
		super((Context) newTripActivityB, _resource, items);
		resource = _resource;
		//this.favoritesProfileB = favoritesProfileB;
	}

	@Override
	public View getView(int position, final View convertView, ViewGroup parent) {
		final LinearLayout newView;
		final int pos = position;

		final Profile profile = getItem(position);

		if (convertView == null) {
			newView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, newView, true);
		} else {
			newView = (LinearLayout) convertView;
		}

		TextView name = (TextView) newView.findViewById(R.id.nameProfile);
		TextView start = (TextView) newView.findViewById(R.id.startPlace);
		TextView end = (TextView) newView.findViewById(R.id.endPlace);

		RelativeLayout relativeLayout = (RelativeLayout) newView.findViewById(R.id.profile_list_layout);

		relativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NewTripActivityB.selectProfile(/**/);
			}
		});

		name.setText(profile.getName());
		start.setText(profile.getStart().getLocation().getAddress());
		end.setText(profile.getEnd().getLocation().getAddress());

		return newView;
	}
}

