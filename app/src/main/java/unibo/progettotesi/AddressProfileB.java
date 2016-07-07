package unibo.progettotesi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddressProfileB extends AppCompatActivity {
	private boolean start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_profile_b);

		start = getIntent().getBooleanExtra("Start", false);
	}

	//set OnKeyListener sull'EditText

	public void setAddress(View v){
		//leggerlo ad alta voce, poi controllare che ritorni una posizione reale, chiedere quanto vuol camminare
		Intent intent = new Intent(this, DistanceB.class);
		intent.putExtra("Start", start);
		startActivity(intent);
		finish();
	}
}
