package unibo.progettotesi.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import unibo.progettotesi.R;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void newProfile(View v){
		Intent intent = new Intent(this, NewProfileActivityB.class);
		intent.putExtra("Start", true);
		startActivity(intent);
	}

	public void newTrip(View v){
		Intent intent = new Intent(this, NewTripActivityB.class);

		startActivity(intent);
	}
}
