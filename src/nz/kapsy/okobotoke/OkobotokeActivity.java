package nz.kapsy.okobotoke;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;



import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;

import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class OkobotokeActivity extends Activity {

	private static final String TAG = "Pd Test";
	private static final String TAG1 = "Pd Debug";

//	private CheckBox left, right, mic;
//	private EditText msg;
//	private Button prefs;
//	private TextView logs;

	private PdService pdService = null;
	private TextView textview1;
	private TextView textview2;

	float bufferSize = 250;
	int sampleRate = 22050;
	int inChan = 0;
	int outChan = 2;
	
	private static final float FM_FADE_MAX = 80F;
	private static final float FM_FADE_MIN = 0F;
	
	
	string test;

	MySurfaceView mysurfview;
	
	
	
	private Toast toast = null;

	private void toast(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
				}
				toast.setText(TAG + ": " + msg);
				toast.show();
			}
		});
	}

//	private void post(final String s) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				logs.append(s + ((s.endsWith("\n")) ? "" : "\n"));
//			}
//		});
//	}
	
//public Context rtnContext() {
//	this.
//	
//}

	private PdReceiver receiver = new PdReceiver() {

		private void pdPost(String msg) {
			toast("Pure Data says, \"" + msg + "\"");
		}

		@Override
		public void print(String s) {
			//pdPost(s);
		}

		@Override
		public void receiveBang(String source) {
				
			if (source.equals("note_change")){
				Log.d("RECV", "bang " + source);
				mysurfview.circle2.init();//.circle.initCircle();
			}
			
			if (source.equals("sonar")){
				Log.d("RECV", "bang " + source);
				
				//mysurfview.sonarcircle.initCircle();

				mysurfview.sonarcircle2.init();
				
			}
									
		}

		@Override
		public void receiveFloat(String source, float x) {
			//pdPost("float: " + x);
		}

		@Override
		public void receiveList(String source, Object... args) {
			//pdPost("list: " + Arrays.toString(args));
		}

		@Override
		public void receiveMessage(String source, String symbol, Object... args) {
			//pdPost("message: " + Arrays.toString(args));
		}

		@Override
		public void receiveSymbol(String source, String symbol) {
			//pdPost("symbol: " + symbol);
		}
	};
	
	
	


	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			pdService = ((PdService.PdBinder)service).getService();
			initPd();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG1, "onCreate() " + System.currentTimeMillis());
		//setContentView(R.layout.main);
		
		
		
		
		//PdPreferences.initPreferences(getApplicationContext());
		//PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
		//initGui();
		
		mysurfview = new MySurfaceView(getApplication());

		//mysurfview = new MySurfaceView(this);
		setContentView(mysurfview);
		
		//高実行すれば、問題が起きて、使えない方がいい
		//setContentView(new MySurfaceView(this));
		
		
		bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);


		//fm_index 9 - 30
/*		SeekBar fmindextester = (SeekBar)this.findViewById(R.id.seekBar1);
		fmindextester.setOnSeekBarChangeListener(new ValChangeAdapter());

		textview1 = (TextView)this.findViewById(R.id.textView1);
		textview2 = (TextView)this.findViewById(R.id.textView2);*/
		


	};
	
	
	@Override
	protected void onPause() {
		Log.d(TAG1, "onPause() " + System.currentTimeMillis());
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
		
		//mysurfview.paused = true;
	}


	@Override
	protected void onResume() {
		Log.d(TAG1, "onResume() " + System.currentTimeMillis());
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		
	//mysurfview.paused = false;

		
	}


	@Override
	protected void onStart() {
		Log.d(TAG1, "onStart() " + System.currentTimeMillis());
		// TODO 自動生成されたメソッド・スタブ
		super.onStart();
	}



	@Override
	protected void onStop() {
		Log.d(TAG1, "onStop() " + System.currentTimeMillis());
		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
	}


	@Override
	protected void onDestroy() {
		Log.d(TAG1, "onDestroy() " + System.currentTimeMillis());
		super.onDestroy();
		cleanup();
	}
	

//	public void gfxStartClick(View v) {
//		
//		
//		
//	}
	
/*	class ValChangeAdapter implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			float f = (float)(progress * 0.21 + 9);
			sendFloat ("fm_index", f);
			textview1.setText("現在の値(フェーダー)：" + progress);
			textview2.setText("現在の値(浮動)：" + f);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}*/




//	@Override
//	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//		startAudio();
//	}

//	private void initGui() {
//		setContentView(R.layout.main);
//		left = (CheckBox) findViewById(R.id.left_box);
//		left.setOnClickListener(this);
//		right = (CheckBox) findViewById(R.id.right_box);
//		right.setOnClickListener(this);
//		mic = (CheckBox) findViewById(R.id.mic_box);
//		mic.setOnClickListener(this);
//		msg = (EditText) findViewById(R.id.msg_box);
//		msg.setOnEditorActionListener(this);
//		prefs = (Button) findViewById(R.id.pref_button);
//		prefs.setOnClickListener(this);
//		logs = (TextView) findViewById(R.id.log_box);
//		logs.setMovementMethod(new ScrollingMovementMethod());
//	}

	private void initPd() {
		//Resources res = getResources();
		//File patchFile = null;



		try {
			PdBase.setReceiver(receiver);
			PdBase.subscribe("note_change");
			PdBase.subscribe("sonar");
//			InputStream in = res.openRawResource(R.raw.test);
//			patchFile = IoUtils.extractResource(in, "test.pd", getCacheDir());
//			PdBase.openPatch(patchFile);

			File dir = getFilesDir();
			File patchFile = new File(dir, "test.pd");
			IoUtils.extractZipResource(getResources().openRawResource(R.raw.patch), dir, true);
			PdBase.openPatch(patchFile.getAbsolutePath());



			startAudio();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			finish();
		}
//		finally {
//			if (patchFile != null) patchFile.delete();
//		}
	}


	private void startAudio() {
		String name = getResources().getString(R.string.app_name);
		try {
			pdService.initAudio(sampleRate, inChan, outChan, bufferSize);   
				// negative values will be replaced with defaults/preferences
			pdService.startAudio(new Intent(this, OkobotokeActivity.class), 
					R.drawable.icon, name, "Return to " + name + ".");
		} catch (IOException e) {
			//toast(e.toString());
			Log.d(TAG, e.toString());
		}
	}

	private void cleanup() {
		try {
			unbindService(pdConnection);
		} catch (IllegalArgumentException e) {
			// already unbound
			pdService = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.about_item:
//			AlertDialog.Builder ad = new AlertDialog.Builder(this);
//			ad.setTitle(R.string.about_title);
//			ad.setMessage(R.string.about_msg);
//			ad.setNeutralButton(android.R.string.ok, null);
//			ad.setCancelable(true);
//			ad.show();
//			break;
//		default:
//			break;
//		}
//		return true;
//	}

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.left_box:
//			PdBase.sendFloat("left", left.isChecked() ? 1 : 0);
//			break;
//		case R.id.right_box:
//			PdBase.sendFloat("right", right.isChecked() ? 1 : 0);
//			break;
//		case R.id.mic_box:
//			PdBase.sendFloat("mic", mic.isChecked() ? 1 : 0);
//			break;
//		case R.id.pref_button:
//			startActivity(new Intent(this, PdPreferences.class));
//			break;
//		default:
//			break;
//		}
//	}

//	@Override
//	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//		evaluateMessage(msg.getText().toString());
//		return true;
//	}

//	private void evaluateMessage(String s) {
//		String dest = "test", symbol = null;
//		boolean isAny = s.length() > 0 && s.charAt(0) == ';';
//		Scanner sc = new Scanner(isAny ? s.substring(1) : s);
//		if (isAny) {
//			if (sc.hasNext()) dest = sc.next();
//			else {
//				toast("Message not sent (empty recipient)");
//				return;
//			}
//			if (sc.hasNext()) symbol = sc.next();
//			else {
//				toast("Message not sent (empty symbol)");
//			}
//		}
//		List<Object> list = new ArrayList<Object>();
//		while (sc.hasNext()) {
//			if (sc.hasNextInt()) {
//				list.add(new Float(sc.nextInt()));
//			} else if (sc.hasNextFloat()) {
//				list.add(sc.nextFloat());
//			} else {
//				list.add(sc.next());
//			}
//		}
//		if (isAny) {
//			PdBase.sendMessage(dest, symbol, list.toArray());
//		} else {
//			switch (list.size()) {
//			case 0:
//				PdBase.sendBang(dest);
//				break;
//			case 1:
//				Object x = list.get(0);
//				if (x instanceof String) {s
//					PdBase.sendSymbol(dest, (String) x);
//				} else {
//					PdBase.sendFloat(dest, (Float) x);
//				}
//				break;
//			default:
//				PdBase.sendList(dest, list.toArray());
//				break;
//			}
//		}
//	}


//	public void sendBang(String s) {
//	  PdBase.sendBang(s);
//	}

	public static void sendFloat(String s, float f) {
		PdBase.sendFloat(s, f);
	}
	
//	public float calcToRange(float sndrmax, float tgtvalmax, float tgtvalmin) {
//		
//		
//		
//	}
	
	public static float calcToRangeFM(float sndrval, float sndrrng) {
		
		float rtnval =(sndrval * (FM_FADE_MAX/sndrrng)) + FM_FADE_MIN;
		return rtnval;
		
	}


//	public void send(String dest, String s) {
//	  String[] pieces = s.split(" ");r
//	  Object[] list = new Object[pieces.length];
//
//	  for (int i=0; i < pieces.length; i++) {
//	    try {
//	      list[i] = Float.parseFloat(pieces[i]);
//	    } catch (NumberFormatException e) {
//	      list[i] = pieces[i];
//	    }
//	  }
//
//	  PdBase.sendList(dest, list);
//	}


}
