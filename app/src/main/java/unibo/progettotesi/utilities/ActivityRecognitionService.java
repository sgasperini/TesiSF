package unibo.progettotesi.utilities;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import unibo.progettotesi.R;
import unibo.progettotesi.activities.BusWaitingActivity;
import unibo.progettotesi.activities.MainActivity;
import unibo.progettotesi.activities.OnTheGoActivity;

public class ActivityRecognitionService extends IntentService {
	public static final String ACTION_GETON = "action_getOn";
	public static final String ACTION_GETOFF = "action_getOff";
	public static Handler activityHandlerBusWaiting;
	public static Handler activityHandlerOnTheGo;
	public static boolean busWaiting;
	public static boolean onTheGo;
	public static NotificationManagerCompat notificationManager;

	public ActivityRecognitionService() {
		super("ActivityRecognitionService");
		Log.wtf( "ActivityRecogition", "dentro il costruttore del servizio" );
		createHandlers();
	}

	private void createHandlers() {
		activityHandlerBusWaiting = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.wtf("ActivityRecognition", "ricevuto bus Waiting");
				busWaiting = true;
				onTheGo = false;
			}
		};
		activityHandlerOnTheGo = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.wtf("ActivityRecognition", "ricevuto otg");
				onTheGo = true;
				busWaiting = false;
			}
		};
	}

	public ActivityRecognitionService(String name) {
		super(name);
		Log.wtf( "ActivityRecogition", "dentro il costruttore nome del servizio" );
		createHandlers();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try{
			if(ActivityRecognitionResult.hasResult(intent)) {
				ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
				handleDetectedActivities( result.getProbableActivities() );
			}
		}catch(Exception e){
			//
		}
	}

	private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
		try{
			for( DetectedActivity activity : probableActivities ) {
				switch( activity.getType() ) {
					case DetectedActivity.IN_VEHICLE: {
						if(busWaiting){
							Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
							//Toast.makeText(ActivityRecognitionService.this, "In Vehicle: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
							if(activity.getConfidence() >= 60) {
								/*NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
								builder.setContentText( "Sei sul bus?" );
								builder.setSmallIcon( R.mipmap.ic_launcher );
								builder.setContentTitle( getString( R.string.app_name ) );
								builder.setVibrate(new long[] { 100, 100, 100, 100, 100 });
								builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

								Intent intent = new Intent(this, OnTheGoActivity.class);
								intent.putExtra("NLeg", BusWaitingActivity.nLeg);
								if(BusWaitingActivity.bus != null)
									intent.putExtra("Bus", BusWaitingActivity.bus);
								PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
								builder.addAction(R.drawable.ic_directions_bus_black_48dp, "Sali", pendingIntent);

								NotificationManagerCompat.from(this).notify(0, builder.build());*/


								createNotification(ACTION_GETON, "Sei sul bus?", "Sali", R.drawable.ic_directions_bus_black_48dp);
							}
						}
						break;
					}
					case DetectedActivity.ON_FOOT: {
						walking(activity);
						break;
					}
					case DetectedActivity.RUNNING: {
						walking(activity);
						break;
					}
					/*case DetectedActivity.STILL: {
						Log.e("ActivityRecogition", "Still: " + activity.getConfidence());
						//Toast.makeText(ActivityRecognitionService.this, "Still: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
						if( activity.getConfidence() >= 75 ) {
							createNotification(ACTION_GETOFF, "Sei fermo?", "Parti", R.drawable.ic_directions_bus_black_48dp_small);
						}
						break;
					}*/
					case DetectedActivity.WALKING: {
						walking(activity);
						break;
					}
				}
			}
		}catch (Exception e){
			//
		}
	}

	private void walking(DetectedActivity activity){
		if(onTheGo){
			Log.e("ActivityRecogition", "Walking: " + activity.getConfidence());
			//Toast.makeText(ActivityRecognitionService.this, "Walking: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
			if(activity.getConfidence() >= 60) {
				createNotification(ACTION_GETOFF, "Sei sceso?", "Scendi", R.drawable.ic_directions_bus_black_48dp);
			}
		}
	}

	private void createNotification(String action, String text, String actionText, int actionIcon){
		Intent actionIntent = new Intent(this, NotificationActionService.class)
				.setAction(action);

		PendingIntent actionPendingIntent = PendingIntent.getService(this, 0,
				actionIntent, PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this)
						.setSmallIcon(R.mipmap.ic_launcher)
						.setContentTitle(getString(R.string.app_name))
						.setContentText(text)
						.addAction(new NotificationCompat.Action(actionIcon,
								actionText, actionPendingIntent))
						.setVibrate(new long[] { 100, 100, 100, 100, 100 })
						.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

		notificationManager = NotificationManagerCompat.from(this);
		Notification notification = notificationBuilder.build();

		notificationManager.notify(0, notification);
	}

	public static class NotificationActionService extends IntentService {
		public NotificationActionService() {
			super(NotificationActionService.class.getSimpleName());
		}

		@Override
		protected void onHandleIntent(Intent intent) {
			if (busWaiting) {
				Log.wtf("Notifica", "salgo");
				BusWaitingActivity.notificationHandler.sendEmptyMessage(0);
			}else if(onTheGo){
				Log.wtf("Notifica", "scendo");
				OnTheGoActivity.notificationHandler.sendEmptyMessage(0);
			}/*else{
				Log.wtf("Notifica", "parto");
				MainActivity.notificationHandler.sendEmptyMessage(0);
			}*/
			notificationManager.cancel(0);
		}
	}
}