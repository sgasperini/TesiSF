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
import unibo.progettotesi.activities.EditDeleteActivityB;
import unibo.progettotesi.activities.FavoritesProfileB;
import unibo.progettotesi.activities.NewTripActivityB;
import unibo.progettotesi.activities.ProfileManagingActivityB;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.utilities.VoiceSupport;


public class ProfilesAdapter extends ArrayAdapter<Profile> {
	private int resource;
	private NewTripActivityB newTripActivityB;
	private EditDeleteActivityB editDeleteActivityB;
	private TextToSpeech tts;
	private boolean edit;

	public ProfilesAdapter(NewTripActivityB newTripActivityB, int _resource, List<Profile> items) {
		super((Context) newTripActivityB, _resource, items);
		resource = _resource;
		this.newTripActivityB = newTripActivityB;
		tts = new TextToSpeech(newTripActivityB.getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.getDefault());
				}
			}
		});
	}

	public ProfilesAdapter(EditDeleteActivityB editDeleteActivityB, int _resource, List<Profile> items, boolean edit) {
		super((Context) editDeleteActivityB, _resource, items);
		resource = _resource;
		this.editDeleteActivityB = editDeleteActivityB;
		this.edit = edit;
		tts = new TextToSpeech(editDeleteActivityB.getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.getDefault());
				}
			}
		});
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
				if(!VoiceSupport.isTalkBackEnabled(getContext()))
					tts.speak("Selezionato " + profile.getName(), TextToSpeech.QUEUE_FLUSH, null);
				if(newTripActivityB != null)
					NewTripActivityB.selectProfile(newTripActivityB, profile);
				else {
					EditDeleteActivityB.selectProfile(editDeleteActivityB, profile, pos);
					if(edit)
						tts.speak("selezionare campo da modificare", TextToSpeech.QUEUE_FLUSH, null);
				}
			}
		});

		name.setText(profile.getName());
		start.setText(profile.getStart().getLocation().getAddress());
		end.setText(profile.getEnd().getLocation().getAddress());

		return newView;
	}
}

