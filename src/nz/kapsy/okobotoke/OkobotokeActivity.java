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
import android.app.Dialog;
import android.app.KeyguardManager.OnKeyguardExitResult;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.EventLog.Event;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
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
		
	private static final float CF_FADE_RNG = 6.5F;
	private static final float CF_FADE_MIN = 1.5F;
	
	FrameLayout framelayout;
	MySurfaceView mysurfview;
	
	//SplashSurfView splashsurfview;
	
	
	
	//TouchView touchview;

	LinearLayout dev_master_btns;
	LinearLayout dev_pref_pg1;
	
	private View splashtest;
	
	private boolean splashinitnosound = true;
	
	
	//private SplashSurfView splashtest;
	
	
	AlphaAnimation fadein;
	
	AlphaAnimation fadeout; 
	
	Context context = this.getApplication();
	
	
	
	private ScheduledExecutorService lightsdelay;
	private Runnable lightrun;
	private ScheduledExecutorService sonardelay;
    private Runnable sonarrun; 
    
    
    
    
    
    
    
    
    
	
	private Toast toast = null;
	
	
	public static boolean destroycalled = false;

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
		
		framelayout = new FrameLayout(this);
		//framelayout.setBackgroundColor(Color.GRAY);
		setContentView(framelayout);
		

		//splashtest = new View(getApplicationContext());
		splashtest = new SplashView(getApplicationContext());
		
		
		//splashtest.setBackgroundColor(Color.LTGRAY);
		
		
		
		
		splashtest.setSoundEffectsEnabled(false);
		
		fadein = new AlphaAnimation(0.0F, 1F);
		fadein.setDuration(900);
		fadein.setStartOffset(500);
		fadein.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {	}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		
		
		
		
		
		
		fadeout = new AlphaAnimation(1F, 0.0F);
		fadeout.setDuration(750);
		fadeout.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {	}

			@Override
			public void onAnimationEnd(Animation animation) {
				framelayout.removeView(splashtest);
				mysurfview.setClickable(true);				
				mysurfview.initDrawables();
				OkobotokeActivity.this.startAudioFade();
				splashinitnosound = false;
			}
		});
		
		
		
		//alpha.get
		
		splashtest.startAnimation(fadein);
		
		

		
		
		
		mysurfview = new MySurfaceView(getApplication());

		mysurfview.setSoundEffectsEnabled(false);
		
		
		splashtest.setClickable(true);
		mysurfview.setClickable(false);
		
		
		
		framelayout.addView(mysurfview);

		framelayout.addView(splashtest);
		
		
		
		
		
		
		
		splashtest.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				splashtest.startAnimation(fadeout);

				return false;
			}
		});
	
				

						
//		dev_master_btns = (LinearLayout)this.getLayoutInflater().inflate(R.layout.dev_master_btns, null);
//		dev_pref_pg1 = (LinearLayout)this.getLayoutInflater().inflate(R.layout.dev_pref_pg1, null);
//		
//		
		//----====----====----====----====----====----
		
//		framelayout.addView(mysurfview, 
//				new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

	//	framelayout.addView(dev_master_btns);
				


		
//		splashsurfview = new SplashSurfView(getApplication());
//		
//		this.setContentView(splashsurfview);
//		
		
		
		
		
		
		
		
		
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		
		
//		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		dialog.getWindow().setAttributes(WindowManager.LayoutParams..FLAG_DIM_BEHIND);
//		myLoadingDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHI‌​ND);
//		Window w = this.getWindow().setAttributes(WindowManager.LayoutParams.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//----====----====----====----====----====----
		
/*		Button dprefbtn_pg1 = (Button)findViewById(R.id.dprefbtn_pg1);
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
			});*/
			
			
			
	
		
		
		
			
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
	



//    // キーイベント発生時、呼び出されます
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_UP) { // キーが離された時
//            switch (event.getKeyCode()) {
//            case KeyEvent.KEYCODE_HOME: // 十字中央キー
//            	
//
//            	
//            	
//                return true;
//            default:
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }
	
	private void startAudioFade() {
		PdAudio.startAudio(this);
		OkobotokeActivity.sendFloat("fm_index", 12F);
		this.sendBang("fade_in");

	}
	

	@Override
	protected void onPause() {
		Log.d(TAG1, "onPause() " + System.currentTimeMillis());

		this.sendBang("fade_out");
		
		try {
			Thread.sleep(900);
		} catch (InterruptedException e) {
					e.printStackTrace();
		}
		
		
		PdAudio.stopAudio();
		super.onPause();

	}
	
	@Override
	protected void onStop() {
		Log.d(TAG1, "onStop() " + System.currentTimeMillis());
		
		this.sendBang("fade_out");
		PdAudio.stopAudio();
		super.onStop();

	}
	
	@Override
	protected void onStart() {
		Log.d(TAG1, "onStart() " + System.currentTimeMillis());


		super.onStart();
		
		

	}
	
	@Override
	protected void onResume() {
		Log.d(TAG1, "onResume() " + System.currentTimeMillis());

		if (!splashinitnosound) {
			this.startAudioFade();
		}

		super.onResume();

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG1, "onDestroy() " + System.currentTimeMillis());
		// this.destroycalled = true;

		this.getWindow().clearFlags
			(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		this.sendBang("fade_out");

		try {
			Thread.sleep(900);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		PdAudio.stopAudio();

		PdAudio.release();
		PdBase.release();

		super.onDestroy();

		// cleanup();
	}
			
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG1, "onSaveInstanceState() " + System.currentTimeMillis());
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(TAG1, "onRestoreInstanceState() " + System.currentTimeMillis());
		super.onRestoreInstanceState(savedInstanceState);
	}







	private void rainStarsInitAll() {
		for(int i = 0; i < mysurfview.rainstars.length; i++) {
			mysurfview.rainstars[i].init();
		}
	}
	
//	private void toast(final String msg) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if (toast == null) {
//					toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//				}
//				toast.setText(TAG + ": " + msg);
//				toast.show();
//			}
//		});
//	}

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
				delayLights();
			}
			
			if (source.equals("sonar")){
				//Log.d("RECV", "bang " + source);
					delaySonar();
			}
			
			if (source.equals("switchdir")) {
				//Log.d("RECV", "bang " + source);
				mysurfview.dirSwitchCalled();
			}
			
//			if (source.equals("fadeoutbang")) {
//				
//				Log.d(TAG1, "fadeoutbang recieved: " + source);
//				
//				pdStopAudio();
//			}
			
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
	    
	    public void delaySonar(){
	    	sonardelay.schedule(sonarrun, 100L, TimeUnit.MILLISECONDS);
	    }
	    
	    public void delayLights(){
	        lightsdelay.schedule(lightrun, 490L, TimeUnit.MILLISECONDS);
	    }
	    
//	    public void pdStopAudio() {
//	    	PdAudio.stopAudio();
//	    	Log.d(TAG1, "pdStopAudio() called via bang");
//	    	
////	    	if (OkobotokeActivity.destroycalled) {
////	    		
////				PdAudio.release();
////				PdBase.release();
////				Log.d(TAG1, "PdAudio.release() and PdBase.release() called via bang");
////	    		
////	    	}
//	    	
//		}

	    
	};
	
	//削除しよう
//	public static void pdStopAudio() {
//    	PdAudio.stopAudio();
//    	Log.d(TAG1, "pdStopAudio() called via bang");
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
		//PdBase.subscribe("fadeoutbang");

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


	public void sendBang(String s) {
		
	  PdBase.sendBang(s);
	  
	}

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