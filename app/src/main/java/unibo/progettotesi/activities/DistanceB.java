package unibo.progettotesi.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import unibo.progettotesi.R;

public class DistanceB extends AppCompatActivity {
	private boolean start;
	private boolean gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance_profile_b);

		start = getIntent().getBooleanExtra("Start", false);
		gps = getIntent().getBooleanExtra("GPS", false);
	}

	//set OnKeyListener sull'EditText

	public void setDistance(View v){
		//leggerla ad alta voce, poi controllare che sia un numero
		//salvare come preferito? andare oltre

		askFavorite();
	}

	private void askFavorite(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage("Vuoi salvare il luogo tra i preferiti?")
				.setCancelable(false)
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//salva
						if(start) {
							Intent intent = new Intent(getApplicationContext(), NewProfileActivityB.class);
							intent.putExtra("Start", !start);
							intent.putExtra("GPS", gps);
							startActivity(intent);
						}else{
							//salva profilo
						}
						dialog.cancel();
						finish();
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(start) {
							Intent intent = new Intent(getApplicationContext(), NewProfileActivityB.class);
							intent.putExtra("Start", !start);
							intent.putExtra("GPS", gps);
							startActivity(intent);
						}else{
							//salva profilo
						}
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}