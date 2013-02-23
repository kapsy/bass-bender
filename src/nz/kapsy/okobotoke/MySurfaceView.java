package nz.kapsy.okobotoke;

import java.util.Random;
import java.util.concurrent.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

public class MySurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {

	private Thread drawthread = null;
	private boolean isattached = false;
	private long timestr = 0, timefin = 0;
	// 1000 / 28 = 35.714 fps
	// 1000 / 22 = 45.4545 fps
	private int threadinterval = 20; 
	
	private SurfaceHolder holder;
	
	private int bgcolor = Color.BLACK;
    
    public Circle2[] maincircles;
    private int curmaincircle = 0;
    // controls root color of main circle lights
    // 今必要ないかも
//    private int maincirclecolcontrol = 400;
    private float maincirclesatcontrol = 1;
    
    // とりあえず
    private boolean burnlights = false;
    
    
        
    public RainStar[] rainstars;
             
    //public NormalCircle foggylight;  
    
    // counts total in array
    private int touchobjs = 2;
    // target like points. not drawn at all.
    // 的みたいな点。全く描いてない。
    public TargetTouch[] targtouchfirst;   
    private int curtargtouchfirst = 0;
    public TargetTouch[] targtouchsecond; 
    private int curtargtouchsecond = 0;
    
    
    public NormalLineFader[] faderline;
    private int curfaderline = 0;
    
      
    public AccelTouch[] acceltouchfirst;
    private int curacceltouchfirst;
    public AccelTouch[] acceltouchsecond;
    private int curacceltouchsecond;
    
    
    public SonarCircle2 sonarcircle2;
       
    
    // symbols etc
    public RecordBar recbar;
	public FrameRecorder framerec = new FrameRecorder();
	
	public RecSymbol recsymbol;
	public PlaySymbol playsymbol;
	public PlaySymbolCntr playsymbolcntr;

	
	//このフィルド名を変えるべき
	private boolean touchenabled = true;
	
	// marking for deletion - helps a little but not worth the headache
//	private boolean secondtouchenabled = true;
//	private int secondtouchdisabledframes = 0;
//	private boolean disableonrel = true; // to ensure that touches are not disabled on release, when a touch is started while disabled.
	
	
	Rect screensizerect;
	public Canvas canvas;
	    
	Bitmap backg_bitmap = 
    		BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.bg_test_2);
	
    private Random rnd = new Random();
    
    // タッチ処理
    final static int MULTI_TOUCH_MAX = 2;
    
    private float screendiag;
    private float screenwidth;
    private float screenheight;
    
    // dev settings page values
    public float radFadearg1 = 1F;
    public float radFadearg2 = 220F;
    public float radFadearg3 = 320F;
    public float radFadearg4 = 250F;

    //not needed
    public float spdaccel1_prf = 1F;
    public float spdaccel2_prf = -.1F;
    public float spdaccel3_prf = -20F;
    public float spdaccel4_prf = 277F;
    

    public void dirSwitchCalled() {
    	    	
    	this.spdaccel3_prf = -10 - rnd.nextInt(20);
    	//速度15-45
    }
    
	public MySurfaceView(Context context) {
        super(context);
        MySurfaceViewInit();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        MySurfaceViewInit();
    }

    public MySurfaceView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        MySurfaceViewInit();
    }

	public void MySurfaceViewInit() {

    	
    	//this.setZOrderOnTop(true);
        holder = getHolder();
        holder.setFormat(PixelFormat.OPAQUE);
        holder.addCallback(this);
        setFocusable(true);
        //requestFocus();        
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
            int format, int width, int height) {
		//Log.d("Pd Debug", "surfaceChanged");	
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
		//Log.d("Pd Debug", "surfaceDestroyed");	
    	isattached = false;
    	this.drawthread = null;
   
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// Log.d("Pd Debug", "surfaceCreated");

		// this.holder = holder;

		// int h = backg_bitmap.getHeight();
		// int w = backg_bitmap.getWidth();
		//
		// float ratio = (float)backg_bitmap.getHeight() /
		// (float)backg_bitmap.getWidth();

		// creates rectangle at ratio of source bitmap to fit screen

	}
    
	public void initDrawables() {

		if (this.isattached == false) {
			
			int rectheight = (int) ((float) getWidth() * ((float) backg_bitmap
					.getHeight() / (float) backg_bitmap.getWidth()));

			// bground = new BackGround();


			screensizerect = new Rect(0, 0, getWidth(), rectheight);

			screendiag = this.getScreenDiag();
			screenwidth = (float) this.getWidth();
			screenheight = (float) this.getHeight();

			maincircles = new Circle2[6];
			for (int i = 0; i < maincircles.length; i++) {
				maincircles[i] = new Circle2();
			}

			sonarcircle2 = new SonarCircle2();

			//foggylight = new NormalCircle();

			// array of touch shapes to allow shapes to fade out
			faderline = new NormalLineFader[this.touchobjs];
			for (int i = 0; i < faderline.length; i++) {
				faderline[i] = new NormalLineFader();
			}
			targtouchfirst = new TargetTouch[this.touchobjs];
			for (int i = 0; i < targtouchfirst.length; i++) {
				targtouchfirst[i] = new TargetTouch();
			}
			targtouchsecond = new TargetTouch[this.touchobjs];
			for (int i = 0; i < targtouchsecond.length; i++) {
				targtouchsecond[i] = new TargetTouch();
			}
			
			acceltouchfirst = new AccelTouch[this.touchobjs];
			for (int i = 0; i < acceltouchfirst.length; i++) {
				acceltouchfirst[i] = new AccelTouch();
				acceltouchfirst[i].setRad(30F);
				acceltouchfirst[i].setBaserad(acceltouchfirst[i].getRad());
				acceltouchfirst[i].setARGB(0, 0, 255, 0);
			}
			acceltouchsecond = new AccelTouch[this.touchobjs];
			for (int i = 0; i < acceltouchsecond.length; i++) {
				acceltouchsecond[i] = new AccelTouch();
				
				acceltouchsecond[i].setRad(20F);
				acceltouchsecond[i].setBaserad(acceltouchsecond[i].getRad());
				acceltouchsecond[i].setARGB(0, 0, 255, 0);
			}
			
			recbar = new RecordBar(this.screenwidth, this.screenheight, 12000,
					threadinterval, this.framerec, this);

			recsymbol = new RecSymbol();
			playsymbol = new PlaySymbol();
			playsymbolcntr = new PlaySymbolCntr();

			rainstars = new RainStar[40];
			for (int i = 0; i < rainstars.length; i++) {
				rainstars[i] = new RainStar();
			}

			this.isattached = true;
			// startThread();
		}

	}
    
    
    public void startThread() {
    	
		//Log.d("Pd Debug", "startThread()");	
        this.drawthread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(isattached) {
					
					timestr = System.currentTimeMillis();
//					Log.d("Pd Debug", "draw() called from drawthread " 
//							+ Thread.currentThread().getName().toString());				
					draw();
					//checkSecondTouch();
			    	
					timefin = System.currentTimeMillis();
					
					long diff = timefin - timestr;
					
					if (diff <= threadinterval) {
											
						try {
							Thread.sleep(threadinterval - diff);
						} 
						catch (InterruptedException e) {}
					}
					else {
						Log.d("time per cycle", "timefin - timestr: " + diff);	
					}
				}
			}
		});

        this.drawthread.start();
    }

    // could use MotionEvent.Obtain for copying events
    // and dispatchTouchEvent(event) for playing back
    // listarray is faster though
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	  
    	TargetTouch tt1 = this.targtouchfirst[this.getCurtargtouchfirst()];
    	TargetTouch tt2 = this.targtouchsecond[this.getCurtargtouchsecond()];
    	
    	NormalLineFader fdr = this.faderline[this.getCurfaderline()]; 
    	
    	AccelTouch at1 = this.acceltouchfirst[this.getCuracceltouchfirst()];
    	AccelTouch at2 = this.acceltouchsecond[this.getCuracceltouchsecond()];
    	
		int pts = event.getPointerCount();
		framerec.setTouchpts(pts);

    	float x0 = event.getX(0);
		float y0 = event.getY(0);
		
   		float x1 = 0F;
		float y1 = 0F;
		
		if (pts == 2) {
			x1 = event.getX(1);
			y1 = event.getY(1);
		}
    	
    	int index = event.getActionIndex();
    	    	Log.v("MotionEvent", "event.getActionIndex() " + event.getActionIndex());
	 			 
			switch (event.getAction() & MotionEvent.ACTION_MASK) {

			case MotionEvent.ACTION_DOWN:
			//	Log.v("MotionEvent", "ACTION_DOWN");

				// why is this here? - want to disable touch straight after rec
				// 他のいい方法があるきっと
				this.touchenabled = true;
				
				// automatically starts recording if touched while playing
				if (this.framerec.isPlayingback()
						|| !this.framerec.isRecordingnow()) {
					this.releaseAllTouchAnims();
			    				    	
					this.framerec.startRecord();
					this.recbar.init();
				}

				// もっといい方法があるはず
				this.nextTargtouchfirst();
				tt1 = this.targtouchfirst[this.getCurtargtouchfirst()];
				
				tt1.setPosX(x0);
				tt1.setPosY(y0);
				//ct1.init();
				
				this.nextAcceltouchfirst();
				at1 = this.acceltouchfirst[this.getCuracceltouchfirst()];
				
				at1.setTargetpoint1(tt1);
				at1.init();
				
				framerec.setMotionevent(MotionEvent.ACTION_DOWN);

				break;

		case MotionEvent.ACTION_POINTER_DOWN:
		//	Log.v("MotionEvent", "ACTION_POINTER_DOWN");

			if (this.touchenabled && pts == MULTI_TOUCH_MAX) {
				
				//if (ct1.getRelAnim() == false)
				if (at1.getRelAnim() == false) {

					this.nextTargtouchsecond();
					tt2 = this.targtouchsecond[this.getCurtargtouchsecond()];

					tt2.setPosX(x1);
					tt2.setPosY(y1);
				//	ct2.init();
				//	ct2.setARGB(0, 0, 255, 255);

					this.nextAcceltouchsecond();
					at2 = this.acceltouchsecond[this.getCuracceltouchsecond()];

					at2.setTargetpoint1(tt2);
					at2.init();

					this.nextFaderline();
					fdr = this.faderline[this.getCurfaderline()];

					fdr.setSnagpoint1(at1);
					fdr.setSnagpoint2(at2);
					fdr.init();

					//this.disableonrel = true;

					framerec.setMotionevent(MotionEvent.ACTION_POINTER_DOWN);
				}

			} /*else {
				this.disableonrel = false;

			}*/
				break;

		case MotionEvent.ACTION_MOVE:
		//	Log.v("MotionEvent", "ACTION_MOVE");

			if (this.touchenabled) {
				// if ct1.getRelAnim()
				if (at1.getRelAnim() == false) {

					tt1.setPosX(x0);
					tt1.setPosY(y0);

					this.sendSingleTouchVals(this.acceltouchfirst
							[this.getCuracceltouchfirst()].getPosX(),
							this.acceltouchfirst[this.getCuracceltouchfirst()].getPosY());

					framerec.setMotionevent(MotionEvent.ACTION_MOVE);

					if (pts == MULTI_TOUCH_MAX) {

						tt2.setPosX(x1);
						tt2.setPosY(y1);
						
						float fdrdist = fdr.calcDistance();
						
						OkobotokeActivity.sendFloat("fm_index",
								OkobotokeActivity.calcToRangeFM(fdrdist, screendiag));
						
						
						
//						float sat = OkobotokeActivity.calcToRangeSaturation(
//								fdrdist,
//								this.screendiag);
//
//				Log.d("saturation", "float sat: " + sat);
//					
//					this.setMaincirclesatcontrol(sat);
//					maincircles[this.getCurmaincircle()].faderSatToCirc(sat);
//						
						
						
						
						
					}
				}
			}
			break;

			case MotionEvent.ACTION_POINTER_UP:
		//		Log.v("MotionEvent", "ACTION_POINTER_UP");

				if (this.touchenabled) {
					if (index == 1) {

						OkobotokeActivity.sendFloat("fm_index", 12F);
						
						//ct2.relAnimOn();
						fdr.relAnimOn();
						
						at2.relAnimOn();
						
/*						if (this.disableonrel) {
							this.secondtouchenabled = false;
						}*/
						
						framerec.setMotionevent(MotionEvent.ACTION_POINTER_UP);

					}
					if (index == 0) {
						this.releaseAllTouchAnims();
						framerec.setMotionevent(MotionEvent.ACTION_POINTER_UP);
					}
				}
				break;

			case MotionEvent.ACTION_UP:
			//	Log.v("MotionEvent", "ACTION_UP");

				if (index == 0) {
	
					// dont understand why i did this... revise
					if (this.touchenabled) {
						//ct1.relAnimOn();
						at1.relAnimOn();
	
						framerec.setMotionevent(MotionEvent.ACTION_UP);
						framerec.setTouchpts(0);
					} else {
						this.touchenabled = false;
					}
				}
	
				break;

			}
			// 指三本処理
			if (pts > MULTI_TOUCH_MAX) {
				this.releaseAllTouchAnims();
			}

		return true;
    }


	protected boolean isTouchenabled() {
		return touchenabled;
	}

	protected void setTouchenabled(boolean touchenabled) {
		this.touchenabled = touchenabled;
	}

	protected static int getMultiTouchMax() {
		return MULTI_TOUCH_MAX;
	}
	
	protected float getScreenwidth() {
		return screenwidth;
	}

	protected float getScreenheight() {
		return screenheight;
	}

	protected void setScreenwidth(float screenwidth) {
		this.screenwidth = screenwidth;
	}

	protected void setScreenheight(float screenheight) {
		this.screenheight = screenheight;
	}

	protected float getScreendiag() {
		return screendiag;
	}
	
	protected void setScreendiag(float screendiag) {
		this.screendiag = screendiag;
	}

	public int rndCol(int scope) {
    	return (255 - rnd.nextInt(scope));
    }
    
    public float getScreenDiag() {
    	
		double w = (double)this.getWidth();
		double h = (double)this.getHeight();
				
	    float diag = (float)Math.sqrt(Math.pow(w, 2D) + Math.pow(h, 2D));
		
	    return diag;
    	
    }
    
public float getMaincirclesatcontrol() {
		return maincirclesatcontrol;
	}

	public void setMaincirclesatcontrol(float maincirclesatcontrol) {
		this.maincirclesatcontrol = maincirclesatcontrol;
	}

	/*	public void checkSecondTouch() {

		if (!this.secondtouchenabled) {

			this.secondtouchdisabledframes++;
			Log.d("MotionEvent","this.secondtouchdisabledframes" + this.secondtouchdisabledframes);
		
			if (this.secondtouchdisabledframes == 12) {
				
				this.secondtouchenabled = true;
				this.secondtouchdisabledframes = 0;
			}

		}

	}*/
	public void draw() {

		canvas = holder.lockCanvas();

		if (canvas != null) {

			//this.debugframecounter++;
			
//			Log.d("Snagcheck", "frame count: " + this.debugframecounter);
			
			
			canvas.drawColor(this.bgcolor);

			for (int i = 0; i < rainstars.length; i++) {
				rainstars[i].drawSequence(canvas);
			}

			this.sonarcircle2.drawSequence(canvas);

			for (int i = 0; i < maincircles.length; i++) {
				maincircles[i].drawSequence(canvas);
			}

			TargetTouch tt1 = this.targtouchfirst[this
					.getCurtargtouchfirst()];
			TargetTouch tt2 = this.targtouchsecond[this
					.getCurtargtouchsecond()];
//			NormalLineFader fdr = this.faderline[this.getCurfaderline()];
//
//			this.framerec.setFrame(tt1.isAlive(), tt1.getPosX(), tt1.getPosY(),
//					tt2.isAlive(), tt2.getPosX(), tt2.getPosY());

			this.framerec.setFrame(tt1.getPosX(), tt1.getPosY(),
					tt2.getPosX(), tt2.getPosY());
			
			
			this.playBackRecording();


//			for (int i = 0; i < circtouchfirst.length; i++) {
//				circtouchfirst[i].drawSequence(canvas);
//			}
//			for (int i = 0; i < circtouchsecond.length; i++) {
//				circtouchsecond[i].drawSequence(canvas);
//			}

			
			
			for (int i = 0; i < acceltouchfirst.length; i++) {
				acceltouchfirst[i].drawSequence(canvas);

//				if (this.acceltouchfirst[i].isAlive()) {
//					Log.d("Snagcheck", 
//							"acceltouchfirst[" + i + "] coords: X: "
//							+ this.acceltouchfirst[i].getPosX() 
//							+ " Y: "
//							+ this.acceltouchfirst[i].getPosY());
//				}
			}
			for (int i = 0; i < acceltouchsecond.length; i++) {
				acceltouchsecond[i].drawSequence(canvas);

//				if (this.acceltouchsecond[i].isAlive()) {
//					Log.d("Snagcheck",
//							"acceltouchsecond[" + i + "] coords: X: "
//							+ this.acceltouchsecond[i].getPosX()
//							+ " Y: "
//							+ this.acceltouchsecond[i].getPosY());
//				}
			}

			// line draw seq MUST be called after touch circle
			// or line will be one frame behind... working on fix for this.
			
			// acceltouch.drawSequence()の後に必ずfaderline.drawsequence()を呼ぶ
			// 呼ばないと繋がった線は一枚のフレームで遅る。
			for (int i = 0; i < faderline.length; i++) {
				faderline[i].drawSequence(canvas);

//				if (this.faderline[i].isAlive()) {
//					Log.d("Snagcheck",
//							"faderline[" + i + "] " + "coords: X1: "
//							+ this.faderline[i].getLinepoints()[0]
//							+ " coords: Y1: "
//							+ this.faderline[i].getLinepoints()[1]
//							+ " coords: X2: "
//							+ this.faderline[i].getLinepoints()[2]
//							+ " coords: Y2: "
//							+ this.faderline[i].getLinepoints()[3]);
//				}
			}	
						

			this.recsymbol.drawSequence(canvas);
			this.playsymbol.drawSequence(canvas);
			this.playsymbolcntr.drawSequence(canvas);
			
			this.recbar.drawSequence(canvas);

			holder.unlockCanvasAndPost(canvas);
		}
	}
    

	public void playBackRecording() {
		if (this.framerec.isPlayingback()) {
		TargetTouch tt1 = this.targtouchfirst[this.getCurtargtouchfirst()];
		TargetTouch tt2 = this.targtouchsecond[this.getCurtargtouchsecond()];
        NormalLineFader fdr = this.faderline[this.getCurfaderline()];
		
    	AccelTouch at1 = this.acceltouchfirst[this.getCuracceltouchfirst()];
    	AccelTouch at2 = this.acceltouchsecond[this.getCuracceltouchsecond()];
		


			FrameRecUnit fru = this.framerec.getPlaybackFrame();
			
			switch (fru.getMotionevent()) {

			// Don't want to do anything when ACTION_CANCEL
			//　多分if(touchpoints > 0)のほうが無難かな
						
			// case MotionEvent.ACTION_CANCEL:

			// break;

			case MotionEvent.ACTION_DOWN:
//				Log.d("playbacktouch", "case MotionEvent.ACTION_DOWN:");
				this.nextTargtouchfirst();
				tt1 = this.targtouchfirst[this.getCurtargtouchfirst()];
				
				tt1.setPosX(fru.getCirtfirstx());
				tt1.setPosY(fru.getCirtfirsty());
				//ct1.init();
				
				this.nextAcceltouchfirst();
				at1 = this.acceltouchfirst[this.getCuracceltouchfirst()];
				
				at1.setTargetpoint1(tt1);
				at1.init();

				break;

			case MotionEvent.ACTION_POINTER_DOWN:
//				Log.d("playbacktouch", "case MotionEvent.ACTION_POINTER_DOWN:");
				
				//if (ct1.getRelAnim() == false) 
				if (at1.getRelAnim() == false) {
					
					this.nextTargtouchsecond();
					tt2 = this.targtouchsecond[this.getCurtargtouchsecond()];

					tt2.setPosX(fru.getCirtsecondx());
					tt2.setPosY(fru.getCirtsecondy());
//					ct2.init();
//					ct2.setARGB(0, 0, 255, 255);

					this.nextAcceltouchsecond();
					at2 = this.acceltouchsecond[this.getCuracceltouchsecond()];

					at2.setTargetpoint1(tt2);
					at2.init();

					this.nextFaderline();
					fdr = this.faderline[this.getCurfaderline()];

					fdr.setSnagpoint1(at1);
					fdr.setSnagpoint2(at2);
					fdr.init();

				}
				break;

			case MotionEvent.ACTION_MOVE:
//				Log.d("playbacktouch", "case MotionEvent.ACTION_MOVE:");
				
							 //if (ct1.getRelAnim() == false) {	
				if (at1.getRelAnim() == false) {
					
					tt1.setPosX(fru.getCirtfirstx());
					tt1.setPosY(fru.getCirtfirsty());
				
//					this.sendSingleTouchVals(fru.getCirtfirstx(), fru.getCirtfirsty());
					
					this.sendSingleTouchVals(this.acceltouchfirst[this.getCuracceltouchfirst()].getPosX(),
							this.acceltouchfirst[this.getCuracceltouchfirst()].getPosY());
					
					
					if (fru.getTouchpts() == 2) {

						tt2.setPosX(fru.getCirtsecondx());
						tt2.setPosY(fru.getCirtsecondy());
						
						OkobotokeActivity.sendFloat("fm_index",
								OkobotokeActivity.calcToRangeFM(
										fdr.calcDistance(),
										screendiag));
						
//						float sat = OkobotokeActivity.calcToRangeSaturation(
//									fdr.calcDistance(),
//									this.screendiag);
//
//						Log.d("saturation", "float sat: " + sat);
//						
//						this.setMaincirclesatcontrol(sat);
//						maincircles[this.getCurmaincircle()].faderSatToCirc(sat);
						
						
					}
				}
				break;

			case MotionEvent.ACTION_POINTER_UP:
//				Log.d("playbacktouch", "case MotionEvent.ACTION_POINTER_UP:");
				//rewrite
				
				//if (fru.getTouchpts() == 2) {
					OkobotokeActivity.sendFloat("fm_index", 12F);
					//ct2.relAnimOn();					
					fdr.relAnimOn();
					at2.relAnimOn();
				//}
				
				
				break;

			case MotionEvent.ACTION_UP:
//				Log.d("playbacktouch", "case MotionEvent.ACTION_UP:");
//				if (fru.getTouchpts() == 0) {
//					this.releaseAllTouchAnims();
//				}
				this.releaseAllTouchAnims();
				
				break;
			}

			// 指三本処理
			if (fru.getTouchpts() > MULTI_TOUCH_MAX) {
//				Log.d("playbacktouch", "if (fru.getTouchpts() > MULTI_TOUCH_MAX)");
				this.releaseAllTouchAnims();
			}
		}
	}

	public void releaseAllTouchAnims() {

		OkobotokeActivity.sendFloat("fm_index", 12F);

		this.faderline[this.getCurfaderline()].relAnimOn();
		this.acceltouchfirst[this.getCuracceltouchfirst()].relAnimOn();
		this.acceltouchsecond[this.getCuracceltouchsecond()].relAnimOn();

//		this.circtouchfirst[this.getCurcirctouchfirst()].relAnimOn();
//		this.circtouchsecond[this.getCurcirctouchsecond()].relAnimOn();

	}
  
	public void nextAllTouchObjs() {
		this.nextAcceltouchfirst();
		this.nextAcceltouchsecond();

		this.nextTargtouchfirst();
		this.nextTargtouchsecond();
		this.nextFaderline();
	}

	public void sendSingleTouchVals(float x, float y) {
		
		OkobotokeActivity.sendFloat(
				"cntr_freq",
				OkobotokeActivity.calcToRangeCentFreq(y, this.screenheight));
		
		OkobotokeActivity.sendFloat(
				"bender",
				OkobotokeActivity.calcToRangeBender(x, this.screenwidth));
		
//		this.setMaincirclecolcontrol
//			(OkobotokeActivity.calcToRangeColor(y, this.screenheight));
		
		//maincircles[this.getCurmaincircle()].calcRGBFromFader();
		

	
		//pitchbend
		
	}

	//cycle through object 配列
	public void nextCirc() {
	
		if(curmaincircle < maincircles.length) {
			curmaincircle++;
		}
		if (curmaincircle == maincircles.length) {
			curmaincircle = 0;
		}
	}
	
	public void nextTargtouchfirst() {
		
		
		
		
		if(curtargtouchfirst < targtouchfirst.length) {
			curtargtouchfirst++;
		}
		if (curtargtouchfirst == targtouchfirst.length) {
			curtargtouchfirst = 0;
		}
	}
    
	public void nextTargtouchsecond() {
		
		if(curtargtouchsecond < targtouchsecond.length) {
			curtargtouchsecond++;
		}
		if (curtargtouchsecond == targtouchsecond.length) {
			curtargtouchsecond = 0;
		}
	}
	
	public void nextAcceltouchfirst() {
		

		
		if(this.curacceltouchfirst < this.acceltouchfirst.length) {
			this.curacceltouchfirst++;
		}
		if (this.curacceltouchfirst == this.acceltouchfirst.length) {
			this.curacceltouchfirst = 0;
		}
		
		Log.d("playbacktouch",
				"this.acceltouchfirst.length " + this.acceltouchfirst.length + 
				"\n" + "this.curacceltouchfirst " + this.curacceltouchfirst);
		
		
		
	}
	
	public void nextAcceltouchsecond() {
		
		if(this.curacceltouchsecond < this.acceltouchsecond.length) {
			this.curacceltouchsecond++;
		}
		if (this.curacceltouchsecond == this.acceltouchsecond.length) {
			this.curacceltouchsecond = 0;
		}
	}
	
	public void nextFaderline() {
		
		if(curfaderline < faderline.length) {
			curfaderline++;
		}
		if (curfaderline == faderline.length) {
			curfaderline = 0;
		}
	}
		
    protected int getCurmaincircle() {
		return curmaincircle;
	}
        
    protected int getCurtargtouchfirst() {
		return curtargtouchfirst;
	}

	protected int getCurtargtouchsecond() {
		return curtargtouchsecond;
	}

	protected int getCurfaderline() {
		return curfaderline;
	}
	
	protected int getCuracceltouchfirst() {
		return curacceltouchfirst;
	}

	protected int getCuracceltouchsecond() {
		return curacceltouchsecond;
	}

/*	protected int getMaincirclecolcontrol() {
		return maincirclecolcontrol;
	}

	protected void setMaincirclecolcontrol(int maincirclecolcontrol) {
		this.maincirclecolcontrol = maincirclecolcontrol;
	}*/

    
    // animatable class extensions
	public class Circle2 extends NormalCircle {
		
		
		//Color test = new Color();
		
		private float[] hsv = {360F, 1F, 1F};
		
		private float satfromfader = 1;
		
		
		
		private AccelTouch targetpoint1;

		
		
		
		
		private float radfad1;
		private float radfad2;
		private float radfad3;
		private float radfad4;
		
		public Circle2() {
			this.setAlive(false);
			this.getPaint().setStyle(Paint.Style.FILL);
			this.getPaint().setAntiAlias(false);
			this.getPaint().setDither(false);
		}
		
    	@Override
    	public void init() {
    		
//    		this.setPosX((float)((int)(r + 10) + rnd.nextInt(getWidth() - (((int)r + 10) * 2))));
//    		this.setPosY((float)((getHeight() / 2) + rnd.nextInt((getHeight() / 2) - 100)));
    		
    		
    		//test.HSVToColor(alpha, hsv);
    		
    		
    		
    		// function of speed 
    		
    		// if cur is alive: if not use last coords
			this.setStartPosRndOffset(40);
			
			this.setYchgspd(0F);
			this.setXchgspd(0F);
			
			this.setMaxaccelspeedx(9F);
			this.setMaxaccelspeedy(9F);
			
    		
    		
    		this.setRad((float)(66 - rnd.nextInt(10)));
    		
    		
    		
    		//this.calcRGBFromFader();  
    		this.setAlpha(0);
    		this.hsv[1] = MySurfaceView.this.getMaincirclesatcontrol();

    		//this.setRadchgspd((float)0.184);
    		//this.setYchgspd((float)-0.674375);
    		this.setYchgspd(-0.3F);
    		this.setLinearyaccelfactor(1.05F);
    		
    		this.setAccelangle(0F);
    		this.setRadfadeangle(0F);
    		this.setBaserad(this.getRad());
    		
    	    		
    		this.radfad1 = radFadearg1;
    		this.radfad2 = radFadearg2;
    		this.radfad3 = radFadearg3;
    		this.radfad4 = radFadearg4;
    		
	
    		super.init();
    	}
    	
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {

				this.getCoordsFromTarget();

				if (!this.getRelAnim()) {

					this.xCalcSpeed((float) getWidth());
					this.yCalcSpeed((float) getWidth());

				} else {

				}

				this.circleAnim();
				//this.calcRGBFromPos();
				
				
				this.calcHSVFromPos();
				
				//今は必要ないかも
				//this.rampSatToFader();
				
				//this.drawCircleFadedEdges(1, 21F, 20, c);
				this.drawCircleOnce(c);
			}
		}
		
		
		@Override
		public void drawCircleOnce(Canvas c) {
			this.getPaint().setColor
					(Color.HSVToColor(this.getAlpha(), this.hsv));
			c.drawCircle(this.getPosX(), this.getPosY(), 
					this.getRad(), this.getPaint());
		}
		
		
		@Override
		public void circleAnim() {

			int cf = this.getCurrframe();

			if (!this.getRelAnim()) {

				if (cf < 600) {

					// this.linearYAccel();
					// this.yIncrement();
					this.alphaIncrement(7.2F, 160F);
				}

			} else {
				if (cf < 150) {
					this.alphaDecrement(3.5F, 0F);
					// this.linearYAccel();
					// this.yIncrement();
				} else if (cf == 150) {
					this.setAlive(false);

				}
			}

			this.radFade(this.radfad1, this.radfad2, this.radfad3, this.radfad4);

			// this.speedAccelSamp(this.spdaccel1, this.spdaccel2,
			// this.spdaccel3,
			// this.spdaccel4);
			this.yIncrement();
			this.xIncrement();
			this.frameAdvance();
		}

		
		private void calcRGBFromPos() {
			//int m = this.calcToRangeColor(this.getPosY(), getHeight());
			
			//OkobotokeActivity.COL_FADE_RNG is 510
			
			int m = (int)(this.getPosY() * (510F/(float)getHeight()));
			int r = 0;
			int b = 255;

			if (m < 0) {
				r = 255;
				m = 0;
			}
			if (m >= 0 && m <= 255) {
				r = 255;
				b = m;
			}
			if (m >= 255 && m <= 510) {
				b = 255;
				r = 255 - (m - 255);
			}
			this.setRGB(r, 0, b);
			//240-360
			//Color.HSVToColor(alpha, hsv)
		}
		
		
		
		
		
//		public int calcToRangeColor(float sndrval, float sndrrng) {
//			int rtnval = (int)(sndrval * (OkobotokeActivity.COL_FADE_RNG/sndrrng));
//			return rtnval;
//			
//		}
// 240～360青いから赤い
		private void calcHSVFromPos() {
			

			int m = (int)(this.getPosY() * (120F/(float)getHeight()));
			
			this.hsv[0] = 360 - m;
			
			
			
		}
		
		private void rampSatToFader() {

			if (this.satfromfader < this.hsv[1]) {
				this.hsv[1] = this.hsv[1] + 0.01f;
			} else if (this.satfromfader > this.hsv[1]) {
				this.hsv[1] = this.hsv[1] - 0.01f;
			}

		}
		
		
		private void faderSatToCirc(float sat) {
			
			
			this.hsv[1] = sat;
			
			
		}
		
		
		
		

		
		
		
		protected AccelTouch getTargetpoint1() {
			return targetpoint1;
		}

		protected void setTargetpoint1(AccelTouch targetpoint1) {

			this.targetpoint1 = targetpoint1;

		}

		protected void getCoordsFromTarget() {
			if (this.targetpoint1 != null) {
				this.setTargetXy(this.targetpoint1.getPosX(),
						this.targetpoint1.getPosY());
			}
		}

		protected void setStartPosRndOffset(int amt) {
			if (this.targetpoint1 != null) {

				float xrnd = (float) (rnd.nextInt(amt * 2) - amt);
				float yrnd = (float) (rnd.nextInt(amt * 2) - amt);

				this.setPosX(this.targetpoint1.getPosX() + xrnd);
				this.setPosY(this.targetpoint1.getPosY() + yrnd);
			}
		}
	}
    
    
    public class SonarCircle2 extends NormalCircle {
    	
    	@Override
    	public void init() {
    		
    		this.setPosX((float)rnd.nextInt(getWidth()));
    		this.setPosY((float)rnd.nextInt(getHeight()));
    		
    		// 89だった
    		this.setARGB(89, 255 - rnd.nextInt(30), 0, 0);
    		
    		this.setRad(20F);
    		this.setRadchgspd(50F);
    		
    		this.getPaint().setStyle(Paint.Style.STROKE);
    		this.getPaint().setStrokeWidth(20F + (float)rnd.nextInt(30));
    		//this.getPaint().setDither(true);
    		
    		//Log.d("SonarCircle2", "init()");
    		
    		super.init();
    	}
    	
    	@Override
		public void drawSequence(Canvas c) {
    		if (this.isAlive()) {
    	        this.circleAnim();
    	      //  this.circleRadiusMod();
    	        this.drawCircleOnce(c);
    		}
		}

		@Override
	    public void circleAnim() {	
    		
    		int cf = this.getCurrframe();
    		//Log.d("SonarAnim", "current frame " + cf);
    		
	    	if (cf < 25){
	    		
	    		this.radIncrement();
    			this.frameAdvance();
    			
	    	}
	    	else if (cf == 25) { 
	    		this.setAlive(false);
	    	}
    	}
    	
    }
    
        
    public class NormalCircleMultiTouch extends NormalCircle {
    	    	
    	@Override
	    public void circleAnim() {	

			int cf = this.getCurrframe();
		
    		if (this.getRelAnim() == false) {
    			
    			if (cf < 500){
		    		this.alphaIncrement(2.6F, 50F);
	    			//Log.d("circleAnim", "alpha val " + this.getAlpha());
	    			this.frameAdvance();
		    	}
		    	else if (cf >= 500 && cf < 600) {
		    		this.frameAdvance();
				}		
		    	else if (cf == 600) { //アニメーション終了
		    		//this.setAlive(false);
		    	}
    				    			
    		}
    		else {
    			if (cf < 20){
    				this.alphaDecrement(2.6F, 0F); 
    				this.frameAdvance();
    				//Log.d("alpha", "A " + this.getAlpha() + " frame " + this.getCurrframe());
    			}
    			else if (cf == 20) { // elseなので、必ず ifが同じ値を
    				this.setAlive(false);
    				//Log.d("isAlive", "thisisAlive " + this.isAlive());
    			}
    		}
	    }
    	
    	@Override
    	public void drawSequence(Canvas c) {
    		if (this.isAlive()) {
    	        this.circleAnim();
    	       this.cirRadModSamp(4.5F, 25F);
    	        this.drawCircleFadedEdges(3, 20F, 1, c);
    		}
    	}
    }
    
    
    public class NormalLineFader extends NormalLine {
    	
    	

    	private boolean startfadeout = false;
    	
    	@Override
    	public void init() {
    		
    		this.setARGB(0, 0, 255, 0);
    		
    		this.getPaint().setStyle(Paint.Style.STROKE);
    		this.getPaint().setStrokeWidth(4F);
    		//this.getPaint().setDither(true);
    		    		
    		super.init();
    	}
    	    	
    	//fader
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				this.setLineBySnags();
				this.circleAnim();
				this.drawLineOnce(c);
			}
		}
		
/*		@Override
		public void circleAnim() {

			int cf = this.getCurrframe();

			if (!this.getRelAnim()) {
				if (cf < 600) {
					this.alphaIncrement(12.4F, 90F);
					this.frameAdvance();
				}
			} else {
				if (cf < 150) {
					this.alphaDecrement(9.3F, 0F);
					this.radIncrement();
					this.frameAdvance();

				} else if (cf == 150) {
					this.setAlive(false);
				}
			}
		}
*/		
		
		
		@Override
		public void circleAnim() {
			//Log.d("AccelTouch", "this.getAlpha() " + this.getAlpha());

			if (!this.getRelAnim()) {

				this.alphaIncrement(5F, 90F);

			} else {

				if (!this.startfadeout) {
					if (this.getAlpha() < 90) {
						this.alphaIncrement(5F, 90F);

					} else {
						this.startfadeout = true;
					}
				}

				if (this.startfadeout) {
					this.alphaDecrement(3.72F, 0F);

					if (this.getAlpha() == 0) {
						this.setAlive(false);
						Log.d("AccelTouch",
								"Alpha 0, this.setAlive(false) called.");
						this.startfadeout = false;
					}
				}
			}

			this.yIncrement();
			this.xIncrement();
			//this.frameAdvance();
		}
		
		
		
		
		
		
		
		
		
		
		
    }
    
    public class RainStar extends NormalCircle {
    	
    	private float radmodspd;
    	private float radmodamp;
    	
    	private float acctargetspd;
    	
		@Override
		public void init() {

			this.setPosX((float) rnd.nextInt(getWidth()));
			this.setPosY((float) rnd.nextInt(getHeight()));

			this.setYchgspd(-0.1F);
			this.setAccelangle(0F);

			this.setRad(5F + (float) rnd.nextInt(20));
			this.setBaserad(this.getRad());
			// this.setRadchgspd(50F);

			this.radmodspd = (float) rnd.nextInt(25) / 10F;
			
			// ensures that modulation is between .25 and .5 the total radius
			// 多分、時間によってこの値を十倍までに増やせばいいんじゃない
			this.radmodamp = this.getRad() * ((float)(10 + rnd.nextInt(25)) / 100F);

			this.acctargetspd = (float) rnd.nextInt(20) / 10F;

			this.setARGB(rnd.nextInt(100), 255, 255, 255);

			this.getPaint().setStyle(Paint.Style.FILL);
			this.getPaint().setStrokeWidth(2F);

			super.init();
		}
		@Override
		public void circleAnim() {
			this.yIncrement();
			this.speedAccelSamp(1F, 0.1F, acctargetspd, 270F);

		}

		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				this.circleAnim();
				this.cirRadModSamp(this.radmodspd, this.radmodamp);
				this.drawCircleOnce(c);
			}
		}
    }
    
    // 点オブジェクトに変える
	public class TargetTouch { //extends NormalCircle {
		private float posx;
		private float posy;
		
		
//		@Override
//		public void init() {
//			this.setARGB(0, 0, 255, 0);
//
//			this.setRad(20F);
//
//			super.init();
//		}

//		@Override
//		public void drawSequence(Canvas c) {
//			if (this.isAlive()) {
//
//				this.circleAnim();
//				this.drawCircleOnce(c);
//			}
//		}

//		@Override
//		public void circleAnim() {
//
//			int cf = this.getCurrframe();
//
//			if (!this.getRelAnim()) {
//
//				if (cf < 600) {
//
//					//this.alphaIncrement(12.4F, 225F);
//				this.alphaIncrement(12.4F, 0F);
//					// Log.d("circleAnim", "alpha val " + this.getAlpha());
//					this.frameAdvance();
//				}
//
//			} else {
//				if (cf < 150) {
//					this.alphaDecrement(9.3F, 0F);
//
//					this.radIncrement();
//					this.frameAdvance();
//
//				} else if (cf == 150) {
//					this.setAlive(false);
//
//				}
//			}
//		}
		
		public float getPosX() {
			return posx;
		}

		public void setPosX(float posx) {
			this.posx = posx;
		}

		public float getPosY() {
			return posy;
		}
		
		public void setPosY(float posy) {
			this.posy = posy;
		}
		
		
	}
    
	public class RecSymbol extends NormalCircle {

		private int blinkratecounter = 0;
		private int alphaonoff = 1;
		
		
		public RecSymbol() {
			
    		this.getPaint().setStyle(Paint.Style.FILL);
    		//this.getPaint().setStrokeWidth(20F + (float)rnd.nextInt(30));
    		this.getPaint().setAntiAlias(true);
		}
		
		@Override
		public void init() {
			this.setAlpha(0);
			this.setBaserad(this.getRad());
			this.blinkratecounter = 0;
			super.init();
		}

		@Override
		public void circleAnim() {

			int cf = this.getCurrframe();

			if (cf < 45) {
				this.alphaIncrement(9.0F, 255F);
				this.blinkFrame(20);
				this.frameAdvance();

			} else if (cf >= 45 && cf < 163) {
				this.blinkFrame(20);
				this.alphaDecrement(2.0F, 20F);
				this.frameAdvance();
			} else if (cf == 163) { // アニメーション終了
				this.setAlive(false);
			}

		}
		
		@Override
		public void drawCircleOnce(Canvas c) {
			this.getPaint().setColor(Color.argb((this.getAlpha() * alphaonoff), 220, 0, 0));
			//c.drawCircle(this.getPosX(), this.getPosY(), this.getRad(), this.getPaint());
			c.drawCircle(70F, ((float)getHeight() - 76F), 34F, this.getPaint());
			//PathMeasure p = new PathMeasure(path, forceClosed);
		}		
		
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				this.circleAnim();
				//this.circleRadiusMod();
				this.drawCircleOnce(canvas);
			}
		}
		
		private void blinkFrame(int rate) {

			int alpha = 1;
			int b = this.blinkratecounter;

			if (b < rate) {
				alpha = 1;
				b++;
			} else if (b >= rate && b < (rate * 2) - 1) {
				alpha = 0;
				b++;
			} else if (this.blinkratecounter == (rate * 2) - 1) {
				alpha = 0;
				b = 0;
			}

			this.blinkratecounter = b;
			this.alphaonoff = alpha;
		}

		protected int getAlphaonoff() {
			return alphaonoff;
		}

		protected void setAlphaonoff(int alphaonoff) {
			this.alphaonoff = alphaonoff;
		}
	}
	
	public class PlaySymbol extends RecSymbol {

		private Path path;
		private PointF[] points = new PointF[3];

		public PlaySymbol() {

			this.getPaint().setStyle(Paint.Style.FILL);
			// this.getPaint().setStrokeWidth(20F + (float)rnd.nextInt(30));
			this.getPaint().setAntiAlias(true);

			this.path = new Path();

			points[0] = new PointF(40F, ((float) getHeight() - 110F));
			points[1] = new PointF(40F, ((float) getHeight() - 42));
			points[2] = new PointF(104F, ((float) getHeight() - 76));

			path.moveTo(points[0].x, points[0].y);
			path.lineTo(points[1].x, points[1].y);
			path.lineTo(points[2].x, points[2].y);
		}

		@Override
		public void circleAnim() {

			int cf = this.getCurrframe();

			if (cf < 45) {
				this.alphaIncrement(9.0F, 255F);
				this.frameAdvance();

			} else if (cf >= 45 && cf < 163) {

				this.alphaDecrement(4.5F, 20F);
				this.frameAdvance();
			} else if (cf == 163) { // アニメーション終了
				this.setAlive(false);
			}
		}

		@Override
		public void drawCircleOnce(Canvas c) {

			this.getPaint().setColor(
					Color.argb((this.getAlpha() * this.getAlphaonoff()), 0, 0, 220));
			c.drawPath(path, this.getPaint());
		}

		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				this.circleAnim();
				// this.circleRadiusMod();
				this.drawCircleOnce(canvas);
			}
		}

		protected Path getPath() {
			return path;
		}

		protected PointF[] getPoints() {
			return points;
		}

		protected void setPath(Path path) {
			this.path = path;
		}

		protected void setPoints(PointF[] points) {
			this.points = points;
		}

	}
	
	public class PlaySymbolCntr extends PlaySymbol {
		
		public PlaySymbolCntr() {

			this.getPaint().setStyle(Paint.Style.FILL);
			this.getPaint().setAntiAlias(true);

			Path path = this.getPath();
			PointF[] points = this.getPoints();

			float cntrx = getWidth() / 2;
			float cntry = getHeight() / 2;
			
			points[0].set(cntrx - 30F, cntry - 50F);
			points[1].set(cntrx - 30F, cntry + 50F);
			points[2].set(cntrx + 70F, cntry);

			path.reset();
			
			path.moveTo(points[0].x, points[0].y);
			path.lineTo(points[1].x, points[1].y);
			path.lineTo(points[2].x, points[2].y);
		}
		
	}
	
    public class AccelTouch extends NormalCircle {
    	
    	private TargetTouch targetpoint1;
    	
    	private boolean startfadeout = false;

		@Override
		public void init() {
			
			this.setPosX(this.targetpoint1.getPosX());
			this.setPosY(this.targetpoint1.getPosY());
			
			this.setYchgspd(0F);
			this.setXchgspd(0F);

			this.getPaint().setStyle(Paint.Style.FILL);
		
			super.init();
		}
		
		@Override
		public void circleAnim() {
			// Log.d("AccelTouch", "this.getAlpha() " + this.getAlpha());

			if (!this.getRelAnim()) {

				this.alphaIncrement(12.4F, 225F);

			} else {

				if (!this.startfadeout) {
					if (this.getAlpha() < 225) {
						this.alphaIncrement(12.4F, 225F);

					} else {
						this.startfadeout = true;
					}
				}

				if (this.startfadeout) {
					this.alphaDecrement(9.3F, 0F);

					if (this.getAlpha() == 0) {
						this.setAlive(false);
						// Log.d("AccelTouch",
							// "Alpha 0, this.setAlive(false) called.");
						this.startfadeout = false;
					}
				}
			}

			this.yIncrement();
			this.xIncrement();
			// this.frameAdvance();
		}
		
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				
				this.getCoordsFromTarget();
				this.xCalcSpeed((float)getWidth());
				this.yCalcSpeed((float)getWidth());
				this.circleAnim();
				this.drawCircleOnce(c);
			}
		}
		
/*		// used because at draw time anims must be updated before line is drawn,
		// but line must be drawn before circles
		public void animSequence(Canvas c) {
			if (this.isAlive()) {
				
				this.getCoordsFromTarget();
				this.xCalcSpeed();
				this.yCalcSpeed();
				this.circleAnim();
			}
		}*/

		protected void getCoordsFromTarget() {
			if (this.targetpoint1 != null) {
				
				this.setTargetXy(this.targetpoint1.getPosX(),  this.targetpoint1.getPosY());
			}
		}
		
		protected TargetTouch getTargetpoint1() {
			return targetpoint1;
		}
		protected void setTargetpoint1(TargetTouch targetpoint1) {
			this.targetpoint1 = targetpoint1;
		}
    }
}


