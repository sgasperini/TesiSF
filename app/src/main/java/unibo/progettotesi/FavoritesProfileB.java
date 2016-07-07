package unibo.progettotesi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FavoritesProfileB extends AppCompatActivity {
	private boolean start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites_profile_b);

		start = getIntent().getBooleanExtra("Start", false);
	}

	public void selectFavorite(View v){
		//leggere ad alta voce il nome
		//conferma
		if(start){
			Intent intent = new Intent(this, NewProfileActivityB.class);
			intent.putExtra("Start", !start);
			startActivity(intent);
			finish();
		}else{
			//salva profilo
		}
	}
}
