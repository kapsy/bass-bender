package nz.kapsy.okobotoke;
// branch test

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nz.kapsy.okobotoke.MySurfaceView.Circle2;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class OkobotokeActivity extends Activity {

	private static final String TAG = "Pd Test";
	private static final String TAG1 = "Pd Debug";
	private static final String RCV = "PdReceiver";

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

	// private static final float COL_SAT_RNG = -1F;
	// private static final float COL_SAT_MIN = 1F;
	
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
	
	private AlphaAnimation splashinitial;
	private AlphaAnimation fadeininfo;
	private AlphaAnimation splashtoinfo;
	private AlphaAnimation splashtoapp; 
	private AlphaAnimation infotoapp;
		
	private ScheduledExecutorService lightsdelay;
	private Runnable lightrun;
	private ScheduledExecutorService sonardelay;
    private Runnable sonarrun; 
    
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

		framelayout.setEnabled(false);
		mysurfview.setEnabled(false);
		infoview.setEnabled(false);
		splashtest.setEnabled(false);
		
		bwlayer.setBackgroundColor(Color.BLACK);
		framelayout.addView(mysurfview);
		
		framelayout.addView(bwlayer);
		framelayout.addView(splashtest);
		
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
					bwlayer.setBackgroundColor(Color.argb(255, 255, 0, 0));
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
	}
	
	private void startAudioFade() {
		PdAudio.startAudio(this);
		OkobotokeActivity.sendFloat("fm_index", 12F);
		sendBang("fade_in");
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG1, "onPause() " + System.currentTimeMillis());

		sendBang("fade_out");
		
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
		super.onPause();
	}

	@Override
	protected void onStop() {
		// Log.d(TAG1, "onStop() " + System.currentTimeMillis());
		super.onStop();
	}

	@Override
	protected void onStart() {
		// Log.d(TAG1, "onStart() " + System.currentTimeMillis());
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
		try {
			Thread.sleep(950);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		PdAudio.stopAudio();
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
		for (int i = 0; i < mysurfview.rainstars.length; i++) {
			mysurfview.rainstars[i].init();
		}
	}


	private PdReceiver receiver = new PdReceiver() {

		private void pdPost(String msg) {
			Log.d(RCV, msg);
		}

		@Override
		public void print(String s) {
			pdPost("print: " + s);
		}

		@Override
		public void receiveBang(String source) {
				
			if (source.equals("notec")){
				delayLights();
			}
			
			if (source.equals("sonar")){
					delaySonar();
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

	private void initPd() throws IOException {

		Log.d("initPd", "initPD() called");

		File dir = getFilesDir();
		File patchFile = new File(dir, "test.pd");

		PdBase.addToSearchPath("/data/data/" + getPackageName() + "/lib");
		PdAudio.initAudio(samplerate, inchan, outchan, 1, true);

		PdBase.setReceiver(receiver);
		PdBase.subscribe("notec");

		PdBase.subscribe("sonar");
		PdBase.subscribe("switchdir");

		IoUtils.extractZipResource(getResources().openRawResource(R.raw.patch),
				dir, true);
		PdBase.openPatch(patchFile.getAbsolutePath());

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public static void sendBang(String s) {
	  PdBase.sendBang(s);
	}

	public static void sendFloat(String s, float f) {
		PdBase.sendFloat(s, f);
	}
	
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

} 