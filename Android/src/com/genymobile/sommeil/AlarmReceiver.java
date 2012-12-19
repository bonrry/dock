package com.genymobile.sommeil;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;



public class AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		try {

			Toast.makeText(context, "C'est l'heure !!!",Toast.LENGTH_LONG).show();
		
			//PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, Reveil.class), 0);
			Intent intent1 = new Intent();
			 intent1.setClass(context, AlarmReceiverActivity.class);
			 intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);
		} catch (Exception r) {
			Toast.makeText(context, "Erreur.",Toast.LENGTH_SHORT).show();
			r.printStackTrace();
		}		
	}
}
