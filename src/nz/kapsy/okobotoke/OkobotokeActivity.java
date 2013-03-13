package nz.kapsy.okobotoke;
// branch test

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nz.kapsy.okobotoke.MySurfaceView.Circle2;
import nz.kapsy.okobotoke.MySurfaceView.TailCircle;

import org.puredata.android.io.AudioParameters;
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
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.rtp.AudioStream;
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
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.Toast;

public class OkobotokeActivity extends Activity {

	private static final String TAG = "Pd Test";
	private static final String TAG1 = "Pd Debug";
	private static final String RCV = "PdReceiver";

	//private PdService pdService = null;
	
	//private int samplerate = 11025;
	private int samplerate = 22050;
	
	private int latencymillis = 100;
	private int inchan = 0;
	private int outchan = 2;

	private static final float FM_FADE_RNG = 90F;
	private static final float FM_FADE_MIN = 0F;
		
	private static final float CF_FADE_RNG = -6.5F;//6.5F;
	private static final float CF_FADE_MIN = 8.0F;//1.5F;
	
	private static final float CF_BENDER_RNG = 220F;
	private static final float CF_BENDER_MIN = 70F;
		
	public static final int COL_FADE_RNG = 510;

//	private static final float COL_SAT_RNG = -1F;
//	private static final float COL_SAT_MIN = 1F;
	
	private static final float COL_SAT_RNG = 255F;
	private static final float COL_SAT_MIN = 0F;

	private static final float PUL_PAN_RNG = 1F;
	private static final float PUL_PAN_MIN = 0F;

	private static final float PUL_FRQ_RNG = -900F;
	private static final float PUL_FRQ_MIN = 1700F;

	FrameLayout framelayout;
	MySurfaceView mysurfview;

	LinearLayout dev_master_btns;
	LinearLayout dev_pref_pg1;

	private boolean splashinitnosound = true;

	// splash 画面
	private View splashtest;
	private InfoView infoview;
	
	private View bwlayer;
	
	private View touchlayertest;
	

//	private AlphaAnimation alphazero;
	private AlphaAnimation splashinitial;
	private AlphaAnimation fadeininfo;
	private AlphaAnimation splashtoinfo;
	private AlphaAnimation splashtoapp; 
	private AlphaAnimation infotoapp;
		
	private ScheduledExecutorService lightsdelay;
	private Runnable lightrun;
	private ScheduledExecutorService sonardelay;
    private Runnable sonarrun; 
    
    //	public static boolean destroycalled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG1, "onCreate() " + System.currentTimeMillis());

		AudioParameters.init(this);

		this.updateLatency();
		SampledSines.init(3600);
		try {
			initPd();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

		lightsdelay = Executors.newSingleThreadScheduledExecutor();
		lightrun = new Runnable() {
			@Override
			public void run() {
				
				
				Circle2 cprev = mysurfview.maincircles[mysurfview.getCurmaincircle()];
				
				cprev.relAnimOn();
				mysurfview.nextCirc();

				Circle2 ccur = mysurfview.maincircles[mysurfview
						.getCurmaincircle()];

				if (mysurfview.framerec.isRecordingnow()) {
					ccur.setTargetpointat(mysurfview.acceltouchfirstrec[mysurfview
							.getCuracceltouchfirstrec()]);

				} else if (mysurfview.framerec.isPlayingback()) {
					ccur.setTargetpointat(mysurfview.acceltouchfirstplay[mysurfview
							.getCuracceltouchfirstplay()]);
				} else {
					ccur.setTargetpointat(mysurfview.getInitialcirclepointer());
				}
				
				
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
		setContentView(framelayout);
		
		splashtest = new SplashView(getApplicationContext());
		splashtest.setSoundEffectsEnabled(false);
		
		infoview = new InfoView(getApplicationContext());
		infoview.setSoundEffectsEnabled(false);
				
		mysurfview = new MySurfaceView(getApplication());
		mysurfview.setSoundEffectsEnabled(false);
		
		bwlayer = new MySurfaceView(getApplication());
		bwlayer.setSoundEffectsEnabled(false);
		
		touchlayertest = new View(getApplicationContext());
		touchlayertest.setSoundEffectsEnabled(false);

		
//		framelayout.setClickable(false);
//		mysurfview.setClickable(false);
//		infoview.setClickable(false);
//		splashtest.setClickable(false);
		
		framelayout.setEnabled(false);
		mysurfview.setEnabled(false);
		infoview.setEnabled(false);
		splashtest.setEnabled(false);
		
		
		//touchlayertest.setClickable(false);

//		dev_master_btns = (LinearLayout)this.getLayoutInflater().inflate(R.layout.dev_master_btns, null);
//		dev_pref_pg1 = (LinearLayout)this.getLayoutInflater().inflate(R.layout.dev_pref_pg1, null);
		
		
		//infoview.setEnabled(false);
		
		
		//infoview.setAlpha(0F);
		
		bwlayer.setBackgroundColor(Color.BLACK);
		
		
		framelayout.addView(mysurfview);
		
	//infoview.setBackgroundColor(Color.TRANSPARENT);
		framelayout.addView(bwlayer);
		framelayout.addView(splashtest);
//framelayout.addView(touchlayertest);
		

		
		splashinitial = new AlphaAnimation(0.0F, 1F);
		splashinitial.setDuration(900);
		splashinitial.setStartOffset(500);
		splashinitial.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				mysurfview.initDrawables();}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				// any gfx pre-processing/loading should go here
				//splashtest.setClickable(true);
				splashtest.setEnabled(true);
			}
		});
		
		
		fadeininfo = new AlphaAnimation(0.0F, 1F);
		fadeininfo.setDuration(900);
		fadeininfo.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				//infoview.setClickable(true);
				infoview.setEnabled(true);
			}
		});
		
		splashtoinfo = new AlphaAnimation(1F, 0.0F);
		splashtoinfo.setDuration(750);
		splashtoinfo.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				framelayout.removeView(splashtest);
				framelayout.addView(infoview);
				infoview.startAnimation(fadeininfo);
			}
		});
		
		splashtoapp = new AlphaAnimation(1F, 0.0F);
		splashtoapp.setDuration(750);
		splashtoapp.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				framelayout.removeView(bwlayer);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				
				framelayout.removeView(splashtest);
				OkobotokeActivity.this.startAudioFade();
				mysurfview.startThread();
				splashinitnosound = false;
				//mysurfview.setClickable(true);		
				mysurfview.setEnabled(true);
				mysurfview.setTouchenabled(true);
			}
		});
		
		infotoapp = new AlphaAnimation(1F, 0.0F);
		infotoapp.setDuration(750);
		infotoapp.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				framelayout.removeView(bwlayer);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				framelayout.removeView(infoview);
				OkobotokeActivity.this.startAudioFade();
				mysurfview.startThread();
				splashinitnosound = false;
				//mysurfview.setClickable(true);	
				mysurfview.setEnabled(true);
				mysurfview.setTouchenabled(true);
			}
		});
		
		splashtest.startAnimation(splashinitial);
		
		splashtest.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getX() < v.getWidth() - SplashView.percToPixX(v.getWidth(), 14F) ||
						event.getY() < v.getHeight() - SplashView.percToPixY(v.getHeight(), 14F)) {
					framelayout.removeView(bwlayer);
					splashtest.startAnimation(splashtoapp);
					
				} else {
					//bwlayer.setBackgroundColor(Color.WHITE);
					bwlayer.setBackgroundColor(Color.argb(255, 160, 0, 255));
					splashtest.startAnimation(splashtoinfo);
				}

				splashtest.setEnabled(false);
				return false;
			}
		});

		infoview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				framelayout.removeView(bwlayer);
				infoview.startAnimation(infotoapp);
				infoview.setEnabled(false);
				return false;
			}
		});
		
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	
	
/*	public void devBtnsInit() {

		Button dprefbtn_pg1 = (Button) findViewById(R.id.dprefbtn_pg1);
		dprefbtn_pg1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d("dprefbtn_pg1", "dev_master_btns.getChildCount()"
						+ dev_master_btns.getChildCount());
				if (dev_master_btns.getChildCount() == 2) {
					dev_master_btns.addView(dev_pref_pg1);
					//devPrefPg1Init();
				}

				else if (dev_master_btns.getChildCount() > 2) {
					dev_master_btns.removeViews(2, 1);
				}
			}
		});

	}
	*/

	



//    // キーイベント発生時、呼び出されます
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_UP) { // キーが離された時
//            switch (event.getKeyCode()) {
//            case KeyEvent.KEYCODE_HOME: // 十字中央キー
//            	
//            	
//                return true;
//            default:
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }
	
	
	private void updateLatency() {
		// bytes

		// apad returns 7680
		// galaxy s returns 3168
		float buffersizebytes = (float) AudioTrack.getMinBufferSize
				(this.samplerate, AudioFormat.CHANNEL_OUT_STEREO,
				AudioFormat.ENCODING_PCM_16BIT);

		// * 2 * 2 - ステリオと１６ビット
		this.latencymillis = (int) (buffersizebytes * 
				(1000F / ((float) this.samplerate * 2F * 2F)));
		
		
		
		/*		//apad returns 1024
		//galaxy s returns 512
		int buffersize = AudioParameters.suggestOutputBufferSize(11025);
		
		//apad returns 1024 
		//galaxy s returns 512
		buffersize = AudioParameters.suggestOutputBufferSize(22050);
		*/
		
	}
	
	private void startAudioFade() {
		PdAudio.startAudio(this);
		OkobotokeActivity.sendFloat("fm_index", 12F);
		this.sendBang("fade_in");

	}
	//sdfsd
	
	

	@Override
	protected void onPause() {
		Log.d(TAG1, "onPause() " + System.currentTimeMillis());

		this.sendBang("fade_out");

		
		//mysurfview.invalidate();
		//mysurfview.releaseAllTouchAnims();
		
		mysurfview.releaseAllTouchPlayAnims();
		mysurfview.releaseAllTouchRecAnims();
		
		
		mysurfview.recbar.fillFramesEmpty();
				mysurfview.framerec.startPlayBack();
		mysurfview.stopThread();
		
		
		try {
			Thread.sleep(900);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		PdAudio.stopAudio();
				

//		
		super.onPause();

	}

	@Override
	protected void onStop() {
		Log.d(TAG1, "onStop() " + System.currentTimeMillis());

//		this.sendBang("fade_out");
//		PdAudio.stopAudio();
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
			mysurfview.initDrawables();
			mysurfview.startThread();
		}

		super.onResume();

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG1, "onDestroy() " + System.currentTimeMillis());
		// this.destroycalled = true;

		this.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
// 必要ないかもonPauseはkならず呼ぶから
//		this.sendBang("fade_out");

		try {
			Thread.sleep(950);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		PdAudio.stopAudio();

		PdAudio.release();
		PdBase.release();

		
		//mysurfview.stopThread();
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
		for (int i = 0; i < mysurfview.rainstars.length; i++) {
			mysurfview.rainstars[i].init();
		}
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
				delayLights();
			}
			
			if (source.equals("sonar")){
				//Log.d("RECV", "bang " + source);
					delaySonar();
			}
			
			if (source.equals("switchdir")) {
				//Log.d("RECV", "bang " + source);
				//mysurfview.dirSwitchCalled();
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

		public void delaySonar() {
			Log.d(TAG1, "OkobotokeActivity.this.latencymillis "
					+ OkobotokeActivity.this.latencymillis);
			Log.d(TAG1, "(int)(OkobotokeActivity.this.latencymillis * 0.7) "
					+ (int) (OkobotokeActivity.this.latencymillis * 0.7));

			sonardelay.schedule(sonarrun,
					(int) (OkobotokeActivity.this.latencymillis * 0.6),
					TimeUnit.MILLISECONDS);
		}

		// 490Lだった
		public void delayLights() {
			lightsdelay.schedule(lightrun,
					(int) (OkobotokeActivity.this.latencymillis * 2.4),
					TimeUnit.MILLISECONDS);
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

		// Resources res = getResources();
		// File patchfile = null;

		// AudioParameters.init(this);
		// int srate = Math.max(MIN_SAMPLE_RATE,
		// AudioParameters.suggestSampleRate());
		
		
				
		File dir = getFilesDir();
		File patchFile = new File(dir, "test.pd");

		// PdBase.addToSearchPath(dir.getAbsolutePath());
		PdBase.addToSearchPath("/data/data/" + getPackageName() + "/lib");
		// PdBase.addToSearchPath("/data/app-lib/" + getPackageName());
		PdAudio.initAudio(samplerate, inchan, outchan, 1, true);

		PdBase.setReceiver(receiver);
		PdBase.subscribe("notec");

		PdBase.subscribe("sonar");
		PdBase.subscribe("switchdir");
		// PdBase.subscribe("fadeoutbang");

		IoUtils.extractZipResource(getResources().openRawResource(R.raw.patch),
				dir, true);
		PdBase.openPatch(patchFile.getAbsolutePath());

		// InputStream in = res.openRawResource(R.raw.count_1);
		// patchfile = IoUtils.extractResource(in, "count_1.pd", getCacheDir());
		// PdBase.openPatch(patchfile);

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


	public static void sendBang(String s) {
		
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
	
	public static float calcToRangeSaturation(float sndrval, float sndrrng) {
		
		float rtnval =(sndrval * (COL_SAT_RNG/sndrrng)) + COL_SAT_MIN;
		return rtnval;
		
	}
	

	public static float calcToRangeCentFreq(float sndrval, float sndrrng) {
		
		float rtnval =(sndrval * (CF_FADE_RNG/sndrrng)) + CF_FADE_MIN;
		return rtnval;
	}
	
	public static float calcToRangeBender(float sndrval, float sndrrng) {
		
		float rtnval =(sndrval * (CF_BENDER_RNG/sndrrng)) + CF_BENDER_MIN;
		return rtnval;
	}
	
	public static int calcToRangeColor(float sndrval, float sndrrng) {
		
		int rtnval = (int)(sndrval * (COL_FADE_RNG/sndrrng));
		
		//Log.d(TAG1, "calcToRangeColor rtnval" + rtnval);
		return rtnval;
		
	}
	
	public static float calcToRangePulsePan(float sndrval, float sndrrng) {
		
		float rtnval =(sndrval * (PUL_PAN_RNG/sndrrng)) + PUL_PAN_MIN;
		return rtnval;
	}
	
	public static float calcToRangePulseFrq(float sndrval, float sndrrng) {
		
		float rtnval =(sndrval * (PUL_FRQ_RNG/sndrrng)) + PUL_FRQ_MIN;
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