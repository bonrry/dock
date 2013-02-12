package com.genymobile.sommeil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;
import android.content.Context;

public class RepeatingAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Uri alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_ALARM);
			if (alert == null) {
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				if (alert == null) {
					alert = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
				}
			}

			Ringtone r = RingtoneManager.getRingtone(context, alert);
			r.play();

			Toast.makeText(context, "C'est l'heure !!!", Toast.LENGTH_LONG)
					.show();

		} catch (Exception r) {
			Toast.makeText(context, "Erreur.", Toast.LENGTH_SHORT).show();
			r.printStackTrace();
		}
	}
}
