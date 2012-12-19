/**
 * 
 */
package com.genymobile.sommeil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

/**
 * @author M-F.P
 * 
 */
public class SplashActivity extends Activity {
	private static final int STOPSPLASH = 0;

	/**
	 * Default duration for the splash screen (milliseconds)
	 */
	private static final long SPLASHTIME = 1200;

	/**
	 * Handler to close this activity and to start automatically
	 * {@link MainActivity} after <code>SPLASHTIME</code> seconds.
	 */
	private final transient Handler splashHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == STOPSPLASH) {
				if (!isFinishing()) {
					final Intent intent = new Intent(SplashActivity.this,
							DashBoardActivity.class);
					startActivity(intent);
					finish();
				}
			}

			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		final Message msg = new Message();
		msg.what = STOPSPLASH;
		splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}
}
