package unibo.progettotesi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class NewProfileActivityB extends AppCompatActivity {
	private boolean start;
	private boolean gpsStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_profile_activity_b);

		start = getIntent().getBooleanExtra("Start", false);
		gpsStart = getIntent().getBooleanExtra("GPS", false);
		//se è stato usato per la partenza il gps levalo
		if(gpsStart)
			findViewById(R.id.gps).setVisibility(View.GONE);
	}

	public void gpsClick(View v){
		// verificare sia attivo (accenderlo), prendere la posizione e mostrarla come indirizzo chiedendo se si vuol salvare come preferito, comunque sia chiedere la distanza a piedi, poi chiamare la nuova activity
		Intent intent = new Intent(this, DistanceB.class);
		intent.putExtra("Start", start);
		intent.putExtra("GPS", true);
		startActivity(intent);
		finish();
	}

	public void favoritesClick(View v){
		Intent intent = new Intent(this, FavoritesProfileB.class);
		intent.putExtra("Start", start);
		startActivity(intent);
		finish();
	}

	public void addressClick(View v){
		Intent intent = new Intent(this, AddressProfileB.class);
		intent.putExtra("Start", start);
		startActivity(intent);
		finish();
	}
}
