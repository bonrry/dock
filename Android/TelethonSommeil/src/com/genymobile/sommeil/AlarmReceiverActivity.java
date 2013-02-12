package com.genymobile.sommeil;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

public class AlarmReceiverActivity extends Activity implements
		TextToSpeech.OnInitListener {
	private TextToSpeech mTts;
	private MediaPlayer mMediaPlayer;
	private PowerManager.WakeLock wl;
	static final int ALARM_ID = 1234567;
	static Alarm alarm;
	private static final Random RANDOM = new Random();
	private static final String[] HELLOS = {
	"prochaine alarme dans dix minutes"

	};

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        wl.acquire();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
			    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
			    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
			    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
			    WindowManager.LayoutParams.FLAG_FULLSCREEN |
			    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
			    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
			    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setContentView(R.layout.alarm);
		mTts = new TextToSpeech(this, this);
		Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
		stopAlarm.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				mMediaPlayer.stop();
				finish();
				return false;
			}
		});

		RelativeLayout fullscreen = (RelativeLayout) findViewById(R.id.fullscreen);
		fullscreen.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				snooze();
				mMediaPlayer.stop();
				finish();
				return false;
			}

		});

		playSound(this, getAlarmUri());
		

	}

	protected void onStop() {
	    super.onStop();
	    wl.release();
	}
	private void playSound(Context context, Uri alert) {
		/*mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IOException e) {
			System.out.println("OOPS");
		}*/
		// recupérer uri
		Log.d("Alarm", "URI Str: "+alert.toString());
		 Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
	        r.play();
	}

	private Uri getAlarmUri() {
		
		// recup from shared
		SharedPreferences sharedPrefs = this.getSharedPreferences("TEST", MODE_PRIVATE);
		String uriStr = sharedPrefs.getString("ring", 
				RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
		
		Log.d("Alarm", "URI Str: "+uriStr);
		Uri alert = Uri.parse(uriStr);
		if (alert == null) {
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}

	/*
	 * Fonction snooze du reveil
	 */
	private void snooze() {
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
				ALARM_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Parler();
		am.cancel(pendingIntent);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (10 * 1000), pendingIntent);
		Toast.makeText(this, "Et c'est parti pour 10 sec de sommeil de plus",
				Toast.LENGTH_LONG).show();

	}

	@Override
	public void onInit(int status) {
		// vérification de la disponibilité de la synthèse vocale.
		if (status == TextToSpeech.SUCCESS) {
			// le choix de la langue ici français
			int result = mTts.setLanguage(Locale.FRANCE);
			// vérification ici si cette langue est supporté par le terminal et
			// si elle existe
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// renvoi une erreur sur la console logcat.
				Log.d( "TAG","Language is not available.");
			}
		} else {
			// si la synthèse vocal n'est pas disponible
			Log.d("TAG", "Could not initialize TextToSpeech.");
		}

	}

	private void Parler() {
		int helloLength = HELLOS.length;
		String hello = HELLOS[RANDOM.nextInt(helloLength)];
		
		mTts.speak(hello, TextToSpeech.QUEUE_FLUSH, null);
	}
}
