package unibo.progettotesi.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.activities.FavoritesProfileB;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.utilities.VoiceSupport;


public class FavoritesAdapter extends ArrayAdapter<Place> {
	private int resource;
	private FavoritesProfileB favoritesProfileB;
	private boolean start;
	private TextToSpeech tts;

	public FavoritesAdapter(FavoritesProfileB favoritesProfileB, int _resource, List<Place> items) {
		super((Context) favoritesProfileB, _resource, items);
		resource = _resource;
		this.favoritesProfileB = favoritesProfileB;

		tts = new TextToSpeech(favoritesProfileB.getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.getDefault());
				}
			}
		});
	}

	public void setStart(boolean start){
		this.start = start;
	}

	@Override
	public View getView(int position, final View convertView, ViewGroup parent) {
		final LinearLayout newView;
		final int pos = position;

		final Place place = getItem(position);

		if (convertView == null) {
			newView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, newView, true);
		} else {
			newView = (LinearLayout) convertView;
		}

		TextView name = (TextView) newView.findViewById(R.id.nameFavorite);
		TextView address = (TextView) newView.findViewById(R.id.placeFavorite);
		RelativeLayout relativeLayout = (RelativeLayout) newView.findViewById(R.id.favoriteLayout);



		relativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!VoiceSupport.isTalkBackEnabled(favoritesProfileB))
					tts.speak("Selezionato " + place.getName(), TextToSpeech.QUEUE_FLUSH, null);
				FavoritesProfileB.selectFavorite(favoritesProfileB, place, start);
			}
		});

		name.setText(place.getName());
		address.setText(place.getLocation().getAddress());

		return newView;
	}
}

