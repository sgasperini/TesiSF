package unibo.progettotesi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.ProfilesAdapter;
import unibo.progettotesi.model.Profile;

public class EditDeleteActivityB extends AppCompatActivity {
	private ProfilesAdapter profilesAdapter;
	private boolean edit;
	private List<Profile> profileList;
	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_trip_activity_b);

		edit = getIntent().getBooleanExtra("Edit", false);
		if(edit)
			Toast.makeText(EditDeleteActivityB.this, "Modifica", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(EditDeleteActivityB.this, "Elimina", Toast.LENGTH_SHORT).show();

		profileList = getProfiles();

		profilesAdapter = new ProfilesAdapter(this, R.layout.profile_b_list, profileList, edit);

		((ListView) findViewById(R.id.listView2)).setAdapter(profilesAdapter);

		tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.getDefault());
				}
			}
		});
	}

	private List<Profile> getProfiles() {
		List<Profile> output = new ArrayList<Profile>();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		int numProfiles = preferences.getInt("NumProfiles", 0);
		for (int i = 1; i <= numProfiles; i++) {
			output.add(Profile.getProfileFromString(preferences.getString("ProfileN_" + i, "")));
		}
		return output;
	}

	private List<Profile> getProfileList(){
		return profileList;
	}

	public static void selectProfile(EditDeleteActivityB editDeleteActivityB, Profile profile, int position){
		//leggere ad alta voce il nome
		//conferma
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(editDeleteActivityB);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		if(editDeleteActivityB.edit){
			Intent intent = new Intent(editDeleteActivityB, EditActivityB.class);
			intent.putExtra("editProfileN", position + 1);
			editDeleteActivityB.startActivity(intent);
			editDeleteActivityB.finish();
		}else {	//delete
			List<Profile> profileList = editDeleteActivityB.getProfileList();
			profileList.remove(position);
			int numProfiles = sharedPreferences.getInt("NumProfiles", 0);
			for (int i = position + 1; i < numProfiles; i++) {
				editor.putString("ProfileN_" + i, profileList.get(i - 1).savingString());
			}
			editor.remove("ProfileN_" + numProfiles);
			editor.putInt("NumProfiles", numProfiles - 1);
			editDeleteActivityB.tts.speak("Profilo eliminato", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(editDeleteActivityB, "Profilo eliminato", Toast.LENGTH_SHORT).show();
			editor.commit();
			editDeleteActivityB.finish();
		}
	}
}
