package unibo.progettotesi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
		//salvare? andare oltre
		if(start){
			Intent intent = new Intent(this, NewProfileActivityB.class);
			intent.putExtra("Start", !start);
			intent.putExtra("GPS", gps);
			startActivity(intent);
			finish();
		}else{
			//salva tutto
		}
	}
}