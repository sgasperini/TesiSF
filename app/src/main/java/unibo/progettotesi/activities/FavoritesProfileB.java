package unibo.progettotesi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.FavoritesAdapter;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.utilities.VoiceSupport;

public class FavoritesProfileB extends AppCompatActivity {
	private boolean start;
	private FavoritesAdapter favoritesAdapter;
	private int editProfileN;
	private boolean departure;
	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_b);

		start = getIntent().getBooleanExtra("Start", false);
		editProfileN = getIntent().getIntExtra("editProfileN", -1);
		if(editProfileN != -1) {
			departure = getIntent().getBooleanExtra("departure", false);
		}

		List<Place> placeList = getFavorites();

		//RIEMPI PLACELIST

		favoritesAdapter = new FavoritesAdapter(this, R.layout.favorite_profile_b_list, placeList);
		favoritesAdapter.setStart(start);

		((ListView) findViewById(R.id.listView)).setAdapter(favoritesAdapter);

		tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.getDefault());
				}
			}
		});
	}

	private List<Place> getFavorites() {
		List<Place> output = new ArrayList<Place>();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		int numFavorites = preferences.getInt("NumFavorites", 0);
		for (int i = 1; i <= numFavorites; i++) {
			output.add(Place.getFavoritePlaceFromString(preferences.getString("FavoriteN_" + i, "")));
		}
		return output;
	}

	public static void selectFavorite(FavoritesProfileB favoritesProfileB, Place favoritePlace, boolean start){
		//leggere ad alta voce il nome
		//conferma
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(favoritesProfileB);
		SharedPreferences.Editor editor = preferences.edit();

		if(favoritesProfileB.editProfileN == -1) {
			if (start)
				editor.putString("StartTempPlace", favoritePlace.savingString());
			else
				editor.putString("EndTempPlace", favoritePlace.savingString());

			editor.commit();

			if (start) {
				Intent intent = new Intent(favoritesProfileB, NewProfileActivityB.class);
				intent.putExtra("Start", !start);
				favoritesProfileB.startActivity(intent);
				favoritesProfileB.tts.speak("Destinazione, selezionare metodo immissione", TextToSpeech.QUEUE_FLUSH, null);
				favoritesProfileB.finish();
			} else {
				Intent intent = new Intent(favoritesProfileB, InputFormB.class);
				intent.putExtra("Start", start);
				intent.putExtra("Name", true);
				intent.putExtra("Favorite", false);
				if(!VoiceSupport.isTalkBackEnabled(favoritesProfileB)) {
					favoritesProfileB.tts.speak("Immettere nome profilo", TextToSpeech.QUEUE_FLUSH, null);
				}
				favoritesProfileB.startActivity(intent);
				favoritesProfileB.finish();
			}
		}else{
			Profile profile = Profile.getProfileFromString(preferences.getString("ProfileN_" + favoritesProfileB.editProfileN, ""));
			if(favoritesProfileB.departure)
				profile.setStart(favoritePlace);
			else
				profile.setEnd(favoritePlace);
			editor.putString("ProfileN_" + favoritesProfileB.editProfileN, profile.savingString());
			editor.commit();
			favoritesProfileB.tts.speak("Profilo modificato", TextToSpeech.QUEUE_FLUSH, null);
			favoritesProfileB.finish();
		}
	}
}
