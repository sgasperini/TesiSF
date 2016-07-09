package unibo.progettotesi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.FavoritesAdapter;
import unibo.progettotesi.model.Place;

public class FavoritesProfileB extends AppCompatActivity {
	private boolean start;
	private FavoritesAdapter favoritesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_b);

		start = getIntent().getBooleanExtra("Start", false);

		List<Place> placeList = getFavorites();

		//RIEMPI PLACELIST

		favoritesAdapter = new FavoritesAdapter(this, R.layout.favorite_profile_b_list, placeList);
		favoritesAdapter.setStart(start);

		((ListView) findViewById(R.id.listView)).setAdapter(favoritesAdapter);
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

		if(start)
			editor.putString("StartTempPlace", favoritePlace.savingString());
		else
			editor.putString("EndTempPlace", favoritePlace.savingString());

		editor.commit();

		if(start){
			Intent intent = new Intent(favoritesProfileB, NewProfileActivityB.class);
			intent.putExtra("Start", !start);
			favoritesProfileB.startActivity(intent);
			favoritesProfileB.finish();
		}else{
			Intent intent = new Intent(favoritesProfileB, InputFormB.class);
			intent.putExtra("Start", start);
			intent.putExtra("Name", true);
			intent.putExtra("Favorite", false);
			favoritesProfileB.startActivity(intent);
			favoritesProfileB.finish();
		}
	}
}
