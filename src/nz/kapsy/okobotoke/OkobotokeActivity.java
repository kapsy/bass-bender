package nz.kapsy.okobotoke;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class OkobotokeActivity extends Activity {

	private static final String TAG = "Pd Test";
	private static final String TAG1 = "Pd Debug";
	private static final String RCV = "PdReceiver";

	//private PdService pdService = null;

	float bufferSize = 50;
	
	int sampleRate = 11025;
	//int sampleRate = 22050;
		
	int inChan = 0;
	int outChan = 2;
	
	private static final float FM_FADE_RNG = 80F;
	private static final float FM_FADE_MIN = 0F;
		
	private static final float CF_FADE_RNG = 5.5F;
	private static final float CF_FADE_MIN = 1.5F;
	
	FrameLayout framelayout;
	MySurfaceView mysurfview;
	
	//TouchView touchview;

	LinearLayout dev_master_btns;
	LinearLayout dev_pref_pg1;
	
	private View blackfade;
	
	private ScheduledExecutorService lightsdelay;
	private Runnable lightrun;
	private ScheduledExecutorService sonardelay;
    private Runnable sonarrun; 
	
	private Toast toast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG1, "onCreate() " + System.currentTimeMillis());

	//	bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);

		try {
			initPd();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		Log.d(TAG1, "onCreate() mark 0 " + System.currentTimeMillis());
		SampledSines.init(3600);
		
		
        lightsdelay = Executors.newSingleThreadScheduledExecutor();
        lightrun = new Runnable() {
        	@Override
			public void run() {
				mysurfview.maincircles[mysurfview.getCurmaincircle()].relAnimOn();
				mysurfview.nextCirc();
				mysurfview.maincircles[mysurfview.getCurmaincircle()].init();
			}
		};
        
        sonardelay = Executors.newSingleThreadScheduledExecutor();
        sonarrun = new Runnable() {
			@Override
			public void run() {	
				mysurfview.sonarcircle2.init();
				rainStarsInitAll();

			}
		};
		
		Log.d(TAG1, "onCreate() mark 1 " + System.currentTimeMillis());
				
		mysurfview = new MySurfaceView(getApplication());
//		touchview = new TouchView(getApplication());
//		
//		touchview.init(mysurfview, mysurfview.framerec);
				
		framelayout = new FrameLayout(this);
						
		dev_master_btns = (LinearLayout)this.getLayoutInflater().inflate(R.layout.dev_master_btns, null);
		dev_pref_pg1 = (LinearLayout)this.getLayoutInflater().inflate(R.layout.dev_pref_pg1, null);
		
		
		//----====----====----====----====----====----
		
		framelayout.addView(mysurfview, 
				new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//framelayout.addView(touchview);

		framelayout.addView(dev_master_btns);
	
		

		setContentView(framelayout);
		Log.d(TAG1, "onCreate() mark 2 " + System.currentTimeMillis());
		
		//----====----====----====----====----====----
		
		Button dprefbtn_pg1 = (Button)findViewById(R.id.dprefbtn_pg1);
			dprefbtn_pg1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					Log.d("dprefbtn_pg1", "dev_master_btns.getChildCount()" + dev_master_btns.getChildCount());
					if (dev_master_btns.getChildCount() == 2) {
						
						dev_master_btns.addView(dev_pref_pg1);
										
						devPrefPg1Init();

					}
					
					else if(dev_master_btns.getChildCount() > 2) {
						
						dev_master_btns.removeViews(2, 1);
					}
	
	
				}
			});	
			
		//----====----====----====----====----====----====----
		
		Button dprefbtn_rec = (Button)findViewById(R.id.dprefbtn_rec);
			dprefbtn_rec.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					mysurfview.framerec.startRecord();
					mysurfview.recbar.init();
					
				}
			});

			Button dprefbtn_play = (Button)findViewById(R.id.dprefbtn_play);
			dprefbtn_play.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					mysurfview.trec.initPlayBack(mysurfview.circtouchsecond, 
//							mysurfview.circtouchfirst, mysurfview.faderline);
					
					//mysurfview.framerec.startPlayBack();
				}
			});
			
			
			
			Log.d(TAG1, "onCreate() mark 3 " + System.currentTimeMillis());
			
			
			
		}
	

	
	public void devPrefPg1Init() {
		
		final EditText radarg1 = (EditText)findViewById(R.id.pg1_et_radfade_arg1);
		final EditText radarg2 = (EditText)findViewById(R.id.pg1_et_radfade_arg2);
		final EditText radarg3 = (EditText)findViewById(R.id.pg1_et_radfade_arg3);
		final EditText radarg4 = (EditText)findViewById(R.id.pg1_et_radfade_arg4);
		
		radarg1.setText(String.valueOf(mysurfview.radFadearg1));
		radarg2.setText(String.valueOf(mysurfview.radFadearg2));
		radarg3.setText(String.valueOf(mysurfview.radFadearg3));
		radarg4.setText(String.valueOf(mysurfview.radFadearg4));
		
		Button pg1_applybtn_radfade = (Button)findViewById(R.id.pg1_applybtn_radfade);
		pg1_applybtn_radfade.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				mysurfview.radFadearg1 = Float.parseFloat(radarg1.getText().toString());
				mysurfview.radFadearg2 = Float.parseFloat(radarg2.getText().toString());
				mysurfview.radFadearg3 = Float.parseFloat(radarg3.getText().toString());
				mysurfview.radFadearg4 = Float.parseFloat(radarg4.getText().toString());
			}
		});
		
		final EditText spdarg1 = (EditText)findViewById(R.id.pg1_et_speedaccelsamp_arg1);
		final EditText spdarg2 = (EditText)findViewById(R.id.pg1_et_speedaccelsamp_arg2);
		final EditText spdarg3 = (EditText)findViewById(R.id.pg1_et_speedaccelsamp_arg3);
		final EditText spdarg4 = (EditText)findViewById(R.id.pg1_et_speedaccelsamp_arg4);
		
		spdarg1.setText(String.valueOf(mysurfview.spdaccel1_prf));
		spdarg2.setText(String.valueOf(mysurfview.spdaccel2_prf));
		spdarg3.setText(String.valueOf(mysurfview.spdaccel3_prf));
		spdarg4.setText(String.valueOf(mysurfview.spdaccel4_prf));
		
		Button pg1_applybtn_spdaccel = (Button)findViewById(R.id.pg1_applybtn_speedaccelsamp);
		pg1_applybtn_spdaccel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				mysurfview.spdaccel1_prf = Float.parseFloat(spdarg1.getText().toString());
				mysurfview.spdaccel2_prf = Float.parseFloat(spdarg2.getText().toString());
				mysurfview.spdaccel3_prf = Float.parseFloat(spdarg3.getText().toString());
				mysurfview.spdaccel4_prf = Float.parseFloat(spdarg4.getText().toString());
			}
		});
				
	}
	

	@Override
	protected void onStart() {
		Log.d(TAG1, "onStart() " + System.currentTimeMillis());
		super.onStart();
		PdAudio.startAudio(this);
	}

	@Override
	protected void onStop() {
		Log.d(TAG1, "onStop() " + System.currentTimeMillis());
		
		PdAudio.stopAudio();
		super.onStop();
	}

	@Override
	protected void onPause() {
		Log.d(TAG1, "onPause() " + System.currentTimeMillis());
		super.onPause();
		PdAudio.stopAudio();
	}

	@Override
	protected void onResume() {
		Log.d(TAG1, "onResume() " + System.currentTimeMillis());
		super.onResume();
		PdAudio.startAudio(this);
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG1, "onDestroy() " + System.currentTimeMillis());
		super.onDestroy();
		PdAudio.release();
		PdBase.release();
		
	//	cleanup();
	}
		
	
	private void rainStarsInitAll() {
		for(int i = 0; i < mysurfview.rainstars.length; i++) {
			mysurfview.rainstars[i].init();
		}
	}
	
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
	private PdReceiver receiver = new PdReceiver() {

		private void pdPost(String msg) {
			//toast("Pure Data says, \"" + msg + "\"");
			
			Log.d(RCV, msg);
		}

		@Override
		public void print(String s) {
			pdPost("print: " + s);
		}

		@Override
		public void receiveBang(String source) {
				
			if (source.equals("notec")){
				//Log.d("RECV", "bang " + source);
				//mysurfview.circle2.init();//.circle.initCircle();
				DelayLights();
				
			}
			
			if (source.equals("sonar")){
				//Log.d("RECV", "bang " + source);
					DelaySonar();

			}
			
			if (source.equals("switchdir")) { 

				//Log.d("RECV", "bang " + source);
				
				mysurfview.dirSwitchCalled();
			}
			
			pdPost("receiveBang: " + source);
									
		}

		@Override
		public void receiveFloat(String source, float x) {
			pdPost("receiveFloat: " + x);
		}

		@Override
		public void receiveList(String source, Object... args) {
			pdPost("receiveList: " + Arrays.toString(args));
		}

		@Override
		public void receiveMessage(String source, String symbol, Object... args) {
			pdPost("receiveMessage: " + Arrays.toString(args));
		}

		@Override
		public void receiveSymbol(String source, String symbol) {
			pdPost("receiveSymbol: " + symbol);
		}
		
		

	    
	    public void DelaySonar(){
	    	sonardelay.schedule(sonarrun, 100L, TimeUnit.MILLISECONDS);
	    }
	    
	    public void DelayLights(){
	        lightsdelay.schedule(lightrun, 490L, TimeUnit.MILLISECONDS);
	    }
	    

	    
	};




	
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

/*	private void initPd() {
		//Resources res = getResources();
		//File patchFile = null;



		try {
			PdBase.setReceiver(receiver);
			PdBase.subscribe("notec");
			//PdBase.subscribe("metbang");
			PdBase.subscribe("sonar");
			PdBase.subscribe("switchdir");
			

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
	}*/

	private void initPd() throws IOException {
		
		Log.d("initPd", "initPD() called");
		
//		Resources res = getResources();
//		File patchfile = null;
		
//		AudioParameters.init(this);
//		int srate = Math.max(MIN_SAMPLE_RATE, AudioParameters.suggestSampleRate());
				File dir = getFilesDir();
		File patchFile = new File(dir, "test.pd");
		
		
		//PdBase.addToSearchPath(dir.getAbsolutePath());
		PdBase.addToSearchPath("/data/data/" + getPackageName() + "/lib");
		//PdBase.addToSearchPath("/data/app-lib/" + getPackageName());
		PdAudio.initAudio(sampleRate, inChan, outChan, 1, true);
		
		

		

		
		PdBase.setReceiver(receiver);
		PdBase.subscribe("notec");

		PdBase.subscribe("sonar");
		PdBase.subscribe("switchdir");
		

		IoUtils.extractZipResource(getResources().openRawResource(R.raw.patch), dir, true);
		PdBase.openPatch(patchFile.getAbsolutePath());
		
		
//		InputStream in = res.openRawResource(R.raw.count_1);
//		patchfile = IoUtils.extractResource(in, "count_1.pd", getCacheDir());
//		PdBase.openPatch(patchfile);
		
	}
	
	

/*	private void startAudio() {
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
	}*/

/*	private void cleanup() {
		try {
			unbindService(pdConnection);
		} catch (IllegalArgumentException e) {
			// already unbound
			pdService = null;
		}
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.main, menu);rn
		
//		if (this.showingops) {			
//			Log.d("showingops", "true");
//			showingops = false;
//			this.setContentView(this.mysurfview);
//
//			
//		}
//		else {
//			Log.d("showingops", "false");
//			showingops = true;
//			this.setContentView(R.layout.devset);
//		}
//		
		
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
		
		float rtnval =(sndrval * (FM_FADE_RNG/sndrrng)) + FM_FADE_MIN;
		return rtnval;
		
	}

	public static float calcToRangeCentFreq(float sndrval, float sndrrng) {
		
		float rtnval =(sndrval * (CF_FADE_RNG/sndrrng)) + CF_FADE_MIN;
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