package nz.kapsy.bassbender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

public class SplashActivity extends Activity {

	private static final String TAG = "SplashActivity lifecycle";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate() " + System.currentTimeMillis());
		
		// タイトルを非表示にします。		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.);
		// splash.xmlをViewに指定します。
		setContentView(R.layout.splash);
		Handler hdl = new Handler();
		// 500ms遅延させてsplashHandlerを実行します。
		hdl.postDelayed(new splashHandler(), 3002);
	}

	class splashHandler implements Runnable {
		public void run() {
			// スプラッシュ完了後に実行するActivityを指定します。
			
			Log.d(TAG, "splashHandler run() " + System.currentTimeMillis());
			Intent intent = new Intent(getApplication(), BassBenderActivity.class);
			startActivity(intent);
			// SplashActivityを終了させますd。
			SplashActivity.this.finish();
		}
	}
}
