package unibo.progettotesi.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.ProfilesAdapter;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.utilities.VoiceSupport;

public class EditDeleteActivityB extends AppCompatActivity {
	private ProfilesAdapter profilesAdapter;
	private boolean edit;
	private List<Profile> profileList;
	private TextToSpeech tts;
	private boolean voiceSupport;
	public static Handler finishHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_trip_activity_b);

		setTitle("Seleziona Profilo");

		edit = getIntent().getBooleanExtra("Edit", false);
		if(edit)
			Toast.makeText(EditDeleteActivityB.this, "Modifica", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(EditDeleteActivityB.this, "Elimina", Toast.LENGTH_SHORT).show();

		profileList = getProfiles();

		profilesAdapter = new ProfilesAdapter(this, R.layout.profile_b_list, profileList, edit);

		((ListView) findViewById(R.id.listView2)).setAdapter(profilesAdapter);

		voiceSupport = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("VoiceSupport", true);

		if(voiceSupport)
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});

		finishHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				finish();
			}
		};
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

	public static void selectProfile(final EditDeleteActivityB editDeleteActivityB, Profile profile, final int position){
		//leggere ad alta voce il nome
		//conferma
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(editDeleteActivityB);
		if(editDeleteActivityB.voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(editDeleteActivityB)){
				editDeleteActivityB.tts.speak("Confermi di " + (editDeleteActivityB.edit ? "modificare" : "eliminare") + " il profilo selezionato?", TextToSpeech.QUEUE_FLUSH, null);
			}
		alertDialogBuilder
				.setTitle("Conferma")
				.setIcon(R.mipmap.ic_launcher)
				.setMessage("Confermi di " + (editDeleteActivityB.edit ? "modificare" : "eliminare") + " il profilo selezionato?")
				.setCancelable(false)
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(editDeleteActivityB);
						SharedPreferences.Editor editor = sharedPreferences.edit();

						if(editDeleteActivityB.edit){
							Intent intent = new Intent(editDeleteActivityB, EditActivityB.class);
							intent.putExtra("editProfileN", position + 1);
							editDeleteActivityB.startActivity(intent);
							if(editDeleteActivityB.voiceSupport)
								editDeleteActivityB.tts.speak("Seleziona il campo da modificare", TextToSpeech.QUEUE_FLUSH, null);
							if(!VoiceSupport.isTalkBackEnabled(editDeleteActivityB))
								Toast.makeText(editDeleteActivityB, "Seleziona il campo da modificare", Toast.LENGTH_SHORT).show();
							//editDeleteActivityB.finish();
						}else {	//delete
							List<Profile> profileList = editDeleteActivityB.getProfileList();
							profileList.remove(position);
							int numProfiles = sharedPreferences.getInt("NumProfiles", 0);
							for (int i = position + 1; i < numProfiles; i++) {
								editor.putString("ProfileN_" + i, profileList.get(i - 1).savingString());
							}
							editor.remove("ProfileN_" + numProfiles);
							editor.putInt("NumProfiles", numProfiles - 1);
							if(editDeleteActivityB.voiceSupport)
								editDeleteActivityB.tts.speak("Profilo eliminato", TextToSpeech.QUEUE_FLUSH, null);
							if(!VoiceSupport.isTalkBackEnabled(editDeleteActivityB))
								Toast.makeText(editDeleteActivityB, "Profilo eliminato", Toast.LENGTH_SHORT).show();
							editor.commit();
							editDeleteActivityB.finish();
						}
						dialog.cancel();
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	protected void onDestroy() {
		if(tts !=null){
			while(tts.isSpeaking()){}
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}
}
