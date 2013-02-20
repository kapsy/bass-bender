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
	private int threadinterval = 22; 
	
	
	private SurfaceHolder holder;
	
	//private BackGround bground;
	private int bgcolor = Color.BLACK;
    
    public Circle2[] maincircles;
    private int curmaincircle = 0;
    // controls root color of main circle lights
    private int maincirclecolcontrol = 400;
        
    public RainStar[] rainstars;
             
    public NormalCircle foggylight;  
    
    public PlainTouchCirc[] circtouchfirst;   
    private int curcirctouchfirst = 0;
    public PlainTouchCirc[] circtouchsecond; 
    private int curcirctouchsecond = 0;
    public NormalLineFader[] faderline;
    private int curfaderline = 0;
    
    
    //public AccelTouch acceltouch1;
    
    public AccelTouch[] acceltouchfirst;
    private int curacceltouchfirst;
    public AccelTouch[] acceltouchsecond;
    private int curacceltouchsecond;
    
    
    
    
    
    
    
    public SonarCircle2 sonarcircle2;
    
    //public BlackFadeLayer blackfadelyr;
	
    public RecordBar recbar;
	public FrameRecorder framerec = new FrameRecorder();
	
	public RecSymbol recsymbol;
	public PlaySymbol playsymbol;

	//このフィルド名を変えるべき
	private boolean touchenabled = true;
	
	// marking for deletion - helps a little but not worth the headache
	private boolean secondtouchenabled = true;
	private int secondtouchdisabledframes = 0;
	private boolean disableonrel = true; // to ensure that touches are not disabled on release, when a touch is started while disabled.
	

	
	Rect screensizerect;
	public Canvas canvas;
	    
	Bitmap backg_bitmap = 
    		BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.bg_test_2);
	
    private Random rnd = new Random();
    
    //private static Random srnd = new Random();
    
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

			// new Rect(l, t, r, b);

			// bground = new BackGround();

			// blackfadelyr = new BlackFadeLayer();
			// blackfadelyr.init();

			screensizerect = new Rect(0, 0, getWidth(), rectheight);

			screendiag = this.getScreenDiag();
			screenwidth = (float) this.getWidth();
			screenheight = (float) this.getHeight();

			maincircles = new Circle2[6];
			for (int i = 0; i < maincircles.length; i++) {
				maincircles[i] = new Circle2();
			}

			sonarcircle2 = new SonarCircle2();

			foggylight = new NormalCircle();

			// array of touch shapes to allow shapes to fade out
			faderline = new NormalLineFader[4];
			for (int i = 0; i < faderline.length; i++) {
				faderline[i] = new NormalLineFader();
			}
			circtouchfirst = new PlainTouchCirc[4];
			for (int i = 0; i < circtouchfirst.length; i++) {
				circtouchfirst[i] = new PlainTouchCirc();
			}
			circtouchsecond = new PlainTouchCirc[4];
			for (int i = 0; i < circtouchsecond.length; i++) {
				circtouchsecond[i] = new PlainTouchCirc();
			}
			
			acceltouchfirst = new AccelTouch[4];
			for (int i = 0; i < acceltouchfirst.length; i++) {
				acceltouchfirst[i] = new AccelTouch();
				
				acceltouchfirst[i].setRad(30F);
				acceltouchfirst[i].setBaserad(acceltouchfirst[i].getRad());
				acceltouchfirst[i].setARGB(0, 0, 255, 0);
			}
			acceltouchsecond = new AccelTouch[4];
			for (int i = 0; i < acceltouchsecond.length; i++) {
				acceltouchsecond[i] = new AccelTouch();
				
				acceltouchsecond[i].setRad(20F);
				acceltouchsecond[i].setBaserad(acceltouchsecond[i].getRad());
				acceltouchsecond[i].setARGB(0, 0, 255, 0);
			}
			
			
//			 acceltouch1 = new AccelTouch();
//			 acceltouch1.init();

			// initbackground = true;
			recbar = new RecordBar(this.screenwidth, this.screenheight, 12000,
					threadinterval, this.framerec, this);

			recsymbol = new RecSymbol();
			playsymbol = new PlaySymbol();

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
					checkSecondTouch();
			    	
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
    	    	    	
    	PlainTouchCirc ct1 = this.circtouchfirst[this.getCurcirctouchfirst()];
    	PlainTouchCirc ct2 = this.circtouchsecond[this.getCurcirctouchsecond()];
    	
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
    	    	//Log.v("MotionEvent", "event.getActionIndex() " + event.getActionIndex());
	 			 
			switch (event.getAction() & MotionEvent.ACTION_MASK) {

			case MotionEvent.ACTION_DOWN:
				//Log.v("MotionEvent", "ACTION_DOWN");

				// why is this here?
				this.touchenabled = true;
				
				// automatically starts recording if touched while playing
				if (this.framerec.isPlayingback()
						|| !this.framerec.isRecordingnow()) {

//					Log.v("MotionEvent", "this.framerec.isPlayingback() " + this.framerec.isPlayingback());
//					Log.v("MotionEvent", "this.framerec.isRecordingnow() " + this.framerec.isRecordingnow());
					
					this.releaseAllTouchAnims();
//					Log.v("MotionEvent", "releaseAllTouchAnims() called");
					
					ct1 = this.circtouchfirst[this.getCurcirctouchfirst()];
			    	ct2 = this.circtouchsecond[this.getCurcirctouchsecond()];
			    	fdr = this.faderline[this.getCurfaderline()]; 
			    	
			    	at1 = this.acceltouchfirst[this.getCuracceltouchfirst()];
			    	at2 = this.acceltouchsecond[this.getCuracceltouchsecond()];
			    				    	
					this.framerec.startRecord();
					this.recbar.init();
				}

				ct1.setPosX(x0);
				ct1.setPosY(y0);
				ct1.init();
				
				at1.setTargetpoint1(ct1);
				at1.init();
				
				framerec.setMotionevent(MotionEvent.ACTION_DOWN);

				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				//    	        	Log.v("MotionEvent", "ACTION_POINTER_DOWN");
				
				if (this.touchenabled && this.secondtouchenabled) {
					if (ct1.getRelAnim() == false) {
		
						ct2.setPosX(x1);
						ct2.setPosY(y1);
						ct2.init();
						ct2.setARGB(0, 0, 255, 255);
						
						
						at2.setTargetpoint1(ct2);
						at2.init();
						
						
						fdr.setSnagpoint1(at1);
						fdr.setSnagpoint2(at2);
						fdr.init();
						
						this.disableonrel = true;
						
						framerec.setMotionevent(MotionEvent.ACTION_POINTER_DOWN);

					}
					
				}
				else {
					this.disableonrel = false;
					
				}
				
				
				break;

			case MotionEvent.ACTION_MOVE:
//				Log.v("MotionEvent", "ACTION_MOVE");

				if (this.touchenabled) {
					
//					Log.v("MotionEvent", "touchenabled " + touchenabled);
					
					if (ct1.getRelAnim() == false) {
						
						ct1.setPosX(x0);
						ct1.setPosY(y0);
						
						//this.sendSingleTouchVals(x0, y0);
						this.sendSingleTouchVals(this.acceltouchfirst[this.getCuracceltouchfirst()].getPosX(),
								this.acceltouchfirst[this.getCuracceltouchfirst()].getPosY());
						
//						this.acceltouch1.setTargetXy(x0, y0);
						
						framerec.setMotionevent(MotionEvent.ACTION_MOVE);

						if (pts == MULTI_TOUCH_MAX && this.secondtouchenabled) {

							ct2.setPosX(x1);
							ct2.setPosY(y1);
							OkobotokeActivity.sendFloat("fm_index",
									OkobotokeActivity.calcToRangeFM(
											fdr.calcDistance(),
											screendiag));
						
						}
					}
				}
				break;

			case MotionEvent.ACTION_POINTER_UP:
				// Log.v("MotionEvent", "ACTION_POINTER_UP");

				if (this.touchenabled) {
					if (index == 1 && this.secondtouchenabled) {

						OkobotokeActivity.sendFloat("fm_index", 12F);
						
						ct2.relAnimOn();
						fdr.relAnimOn();
						this.nextCirctouchsecond();
						this.nextFaderline();
						
						at2.relAnimOn();
						this.nextAcceltouchsecond();
						
						
						if (this.disableonrel) {
							this.secondtouchenabled = false;
						}
						

						framerec.setMotionevent(MotionEvent.ACTION_POINTER_UP);

					}
					if (index == 0) {
						this.releaseAllTouchAnims();
						framerec.setMotionevent(MotionEvent.ACTION_POINTER_UP);
					}
				}
				break;

			case MotionEvent.ACTION_UP:
				// Log.v("MotionEvent", "ACTION_UP");

				if (index == 0) {
	
					// dont understand why i did this... revise
					if (this.touchenabled) {
						ct1.relAnimOn();
						this.nextCirctouchfirst();
						
						at1.relAnimOn();
						this.nextAcceltouchfirst();
	
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
    
	public void checkSecondTouch() {

		if (!this.secondtouchenabled) {

			this.secondtouchdisabledframes++;
			Log.d("MotionEvent","this.secondtouchdisabledframes" + this.secondtouchdisabledframes);
			
			
			if (this.secondtouchdisabledframes == 4) {
				
				this.secondtouchenabled = true;
				this.secondtouchdisabledframes = 0;
			}

		}

	}
    


	public void draw() {

		canvas = holder.lockCanvas();

		if (canvas != null) {

			canvas.drawColor(this.bgcolor);

			for (int i = 0; i < rainstars.length; i++) {
				rainstars[i].drawSequence(canvas);
			}

			this.sonarcircle2.drawSequence(canvas);

			for (int i = 0; i < maincircles.length; i++) {
				maincircles[i].drawSequence(canvas);
			}

			// add later
			// foggylight.drawSequence(canvas);

			PlainTouchCirc ct1 = this.circtouchfirst[this
					.getCurcirctouchfirst()];
			PlainTouchCirc ct2 = this.circtouchsecond[this
					.getCurcirctouchsecond()];
			NormalLineFader fdr = this.faderline[this.getCurfaderline()];

			this.framerec.setFrame(ct1.isAlive(), ct1.getPosX(), ct1.getPosY(),
					ct2.isAlive(), ct2.getPosX(), ct2.getPosY());

			this.playBackRecording();


			for (int i = 0; i < circtouchfirst.length; i++) {
				circtouchfirst[i].drawSequence(canvas);
			}
			for (int i = 0; i < circtouchsecond.length; i++) {
				circtouchsecond[i].drawSequence(canvas);
			}
			
			for (int i = 0; i < acceltouchfirst.length; i++) {
				acceltouchfirst[i].drawSequence(canvas);
			}
			for (int i = 0; i < acceltouchsecond.length; i++) {
				acceltouchsecond[i].drawSequence(canvas);
			}
						for (int i = 0; i < faderline.length; i++) {
				faderline[i].drawSequence(canvas);
			}

//			 this.acceltouch1.drawSequence(canvas);

			this.recsymbol.drawSequence(canvas);
			this.playsymbol.drawSequence(canvas);
			this.recbar.drawSequence(canvas);

			holder.unlockCanvasAndPost(canvas);
		}
	}
    

	public void playBackRecording() {

        PlainTouchCirc ct1 = this.circtouchfirst[this.getCurcirctouchfirst()];
        PlainTouchCirc ct2 = this.circtouchsecond[this.getCurcirctouchsecond()];
        NormalLineFader fdr = this.faderline[this.getCurfaderline()];
		
    	AccelTouch at1 = this.acceltouchfirst[this.getCuracceltouchfirst()];
    	AccelTouch at2 = this.acceltouchsecond[this.getCuracceltouchsecond()];
        

		if (this.framerec.isPlayingback()) {

			FrameRecUnit fru = this.framerec.getPlaybackFrame();
			
			switch (fru.getMotionevent()) {

			// Don't want to do anything when ACTION_CANCEL
			//　多分if(touchpoints > 0)のほうが無難かな
						
			// case MotionEvent.ACTION_CANCEL:

			// break;

			case MotionEvent.ACTION_DOWN:

				ct1.setPosX(fru.getCirtfirstx());
				ct1.setPosY(fru.getCirtfirsty());

				ct1.init();
				
				at1.setTargetpoint1(ct1);
				at1.init();

				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				
				if (ct1.getRelAnim() == false) {
					
					ct2.setPosX(fru.getCirtsecondx());
					ct2.setPosY(fru.getCirtsecondy());
					
					ct2.init();
					ct2.setARGB(0, 0, 255, 255);

					at2.setTargetpoint1(ct2);
					at2.init();
					
					fdr.setSnagpoint1(at1);
					fdr.setSnagpoint2(at2);


					fdr.init();

				}
				break;

			case MotionEvent.ACTION_MOVE:

				if (ct1.getRelAnim() == false) {
					
					ct1.setPosX(fru.getCirtfirstx());
					ct1.setPosY(fru.getCirtfirsty());
				
//					this.sendSingleTouchVals(fru.getCirtfirstx(), fru.getCirtfirsty());
					
					this.sendSingleTouchVals(this.acceltouchfirst[this.getCuracceltouchfirst()].getPosX(),
							this.acceltouchfirst[this.getCuracceltouchfirst()].getPosY());
					
					
					
					
					
					
					if (fru.getTouchpts() == 2) {

						ct2.setPosX(fru.getCirtsecondx());
						ct2.setPosY(fru.getCirtsecondy());
						
						OkobotokeActivity.sendFloat("fm_index",
								OkobotokeActivity.calcToRangeFM(
										fdr.calcDistance(),
										screendiag));
					}
				}
				break;

			case MotionEvent.ACTION_POINTER_UP:
				//rewrite
/*				if (!fru.isCirtsecondisalive()) {
					OkobotokeActivity.sendFloat("fm_index", 12F);
					
					ct2.relAnimOn();					
					fdr.relAnimOn();
					this.nextCirctouchsecond();
					this.nextFaderline();
				} 
				else if (fru.getTouchpts() <= 2) {
					this.releaseAllTouchAnims();
				}*/
				
				//if (fru.getTouchpts() == 2) {
					OkobotokeActivity.sendFloat("fm_index", 12F);
					
					ct2.relAnimOn();					
					fdr.relAnimOn();
					this.nextCirctouchsecond();
					this.nextFaderline();
					
					at2.relAnimOn();
					this.nextAcceltouchsecond();
				//}
				
				
				break;

			case MotionEvent.ACTION_UP:
				if (fru.getTouchpts() == 0) {
					this.releaseAllTouchAnims();
				}
				break;
			}

			// 指三本処理
			if (fru.getTouchpts() > MULTI_TOUCH_MAX) {
				this.releaseAllTouchAnims();
			}
		}
	}
	
	public void releaseAllTouchAnims() {
		
		OkobotokeActivity.sendFloat("fm_index", 12F);
		

		
		
		this.circtouchfirst[this.getCurcirctouchfirst()].relAnimOn();
	    this.circtouchsecond[this.getCurcirctouchsecond()].relAnimOn();
	    this.faderline[this.getCurfaderline()].relAnimOn();

		this.nextCirctouchfirst();
		this.nextCirctouchsecond();
		this.nextFaderline();
		
		this.acceltouchfirst[this.getCuracceltouchfirst()].relAnimOn();
		this.acceltouchsecond[this.getCuracceltouchsecond()].relAnimOn();
		this.nextAcceltouchfirst();
		this.nextAcceltouchsecond();
				
	}
  
	
	public void sendSingleTouchVals(float x, float y) {
		
		OkobotokeActivity.sendFloat(
				"cntr_freq",
				OkobotokeActivity.calcToRangeCentFreq(y, this.screenheight));
		
		OkobotokeActivity.sendFloat(
				"bender",
				OkobotokeActivity.calcToRangeBender(x, this.screenwidth));
		
		this.setMaincirclecolcontrol
			(OkobotokeActivity.calcToRangeColor(y, this.screenheight));
		
		maincircles[this.getCurmaincircle()].calcRGBFromFader();
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
	
	public void nextCirctouchfirst() {
		
		if(curcirctouchfirst < circtouchfirst.length) {
			curcirctouchfirst++;
		}
		if (curcirctouchfirst == circtouchfirst.length) {
			curcirctouchfirst = 0;
		}
	}
    
	public void nextCirctouchsecond() {
		
		if(curcirctouchsecond < circtouchsecond.length) {
			curcirctouchsecond++;
		}
		if (curcirctouchsecond == circtouchsecond.length) {
			curcirctouchsecond = 0;
		}
	}
	
	public void nextAcceltouchfirst() {
		
		if(this.curacceltouchfirst < this.acceltouchfirst.length) {
			this.curacceltouchfirst++;
		}
		if (this.curacceltouchfirst == this.acceltouchfirst.length) {
			this.curacceltouchfirst = 0;
		}
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
        
    protected int getCurcirctouchfirst() {
		return curcirctouchfirst;
	}

	protected int getCurcirctouchsecond() {
		return curcirctouchsecond;
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

	protected int getMaincirclecolcontrol() {
		return maincirclecolcontrol;
	}

	protected void setMaincirclecolcontrol(int maincirclecolcontrol) {
		this.maincirclecolcontrol = maincirclecolcontrol;
	}

    
    // animatable class extensions
    
	public class Circle2 extends NormalCircle {
		
		//private boolean acceldir;
		
		private float radfad1;
		private float radfad2;
		private float radfad3;
		private float radfad4;
		
		
		private float spdaccel1;
		private float spdaccel2;
		private float spdaccel3;
		private float spdaccel4;
		
    	@Override
    	public void init() {
    		Log.d("Circle2", "init()");
    		float r = this.getRad();
    		
    		this.setPosX((float)((int)(r + 10) + rnd.nextInt(getWidth() - (((int)r + 10) * 2))));
    		this.setPosY((float)((int)(r + 200) + rnd.nextInt(getHeight() - (((int)r + 100) + 200))));
    		this.setRad((float)(66 - rnd.nextInt(10)));
    		
    		//this.setARGB(0, 0, 0, 255);
    		this.calcRGBFromFader();  
    		this.setAlpha(0);
    		
//    		Log.d("Circle2", 
//    				"this.getRed() " + this.getRed() +
//    				"this.getBlu() " + this.getBlu());
//    		
    		//this.setRadchgspd((float)0.184);
    		//this.setYchgspd((float)-0.674375);
    		this.setYchgspd(-0.1F);
    		this.setAccelangle(0F);
    		this.setRadfadeangle(0F);
    		this.setBaserad(this.getRad());
    		
    	    		
    		this.radfad1 = radFadearg1;
    		this.radfad2 = radFadearg2;
    		this.radfad3 = radFadearg3;
    		this.radfad4 = radFadearg4;
    		
    		this.spdaccel1 = spdaccel1_prf;
    		this.spdaccel2 = spdaccel2_prf;
    		this.spdaccel3 = spdaccel3_prf - ((float)rnd.nextInt(20));
    		this.spdaccel4 = spdaccel4_prf;
    		
    		this.getPaint().setStyle(Paint.Style.FILL);
    		this.getPaint().setDither(false);
//    		this.getPaint().setAntiAlias(true);
    		
//    		if (MySurfaceView.isParentdirswitch()) {
//    			acceldir = true;
//    		}
//    		else {
//    			acceldir = false;
//    		}
//    		
    		super.init();
    	}
    	
    	@Override
		public void drawSequence(Canvas c) {
    		if (this.isAlive()) {
    	        this.circleAnim();
    	      //  this.circleRadiusMod();
    	        //this.drawCircleFadedEdges(15, 5F, 1, c);
    	        this.drawCircleFadedEdges(3, 21F, 20, c);
    	        
    		}
		}
    
		@Override
		public void circleAnim() {

			int cf = this.getCurrframe();

			if (!this.getRelAnim()) {

				if (cf < 600) {
					this.radIncrement();
					this.yIncrement();
					// 9.6F
					this.alphaIncrement(7.2F, 110F);
					// this.alphaDecrement(0.3F, 0F);
					// Log.d("circleAnim", "alpha val " + this.getAlpha());
					this.frameAdvance();
				}
				// else if (cf >= 500 && cf < 600) {
				// this.frameAdvance();
				// }
				else if (cf == 600) { // アニメーション終了
					// this.setAlive(false);
				}

			} else {
				if (cf < 150) {
					this.alphaDecrement(3.5F, 0F);

					this.radIncrement();
					this.yIncrement();
					this.frameAdvance();
					// Log.d("alpha", "A " + this.getAlpha() + " frame " +
					// this.getCurrframe());
				} else if (cf == 150) {
					this.setAlive(false);
					// Log.d("setAlive", "thisisAlive " + this.isAlive());
				}
			}

			// if (MySurfaceView.isParentdirswitch()) {
			this.radFade(this.radfad1, this.radfad2, this.radfad3, this.radfad4);
			// }

			this.speedAccelSamp(this.spdaccel1, this.spdaccel2, this.spdaccel3,
					this.spdaccel4);

		}
		
		
		private void calcRGBFromFader() {
			int m = MySurfaceView.this.getMaincirclecolcontrol();
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
    		
    		Log.d("SonarCircle2", "init()");
    		
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
    				Log.d("isAlive", "thisisAlive " + this.isAlive());
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
    	
    	@Override
    	public void init() {
    		
    		this.setARGB(0, 0, 255, 0);
    		
    		this.getPaint().setStyle(Paint.Style.STROKE);
    		this.getPaint().setStrokeWidth(4F);
    		//this.getPaint().setDither(true);
    		    		
    		super.init();
    	}
    	    	
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				this.setLineBySnags();
				this.circleAnim();
				this.drawLineOnce(c);
			}
		}
		
		@Override
		public void circleAnim() {

			int cf = this.getCurrframe();

			if (!this.getRelAnim()) {
				if (cf < 600) {
//					this.alphaIncrement(12.4F, 0F);
					this.alphaIncrement(12.4F, 225F);
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

			// int gry = rnd.nextInt(200);

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
    
    public class PlainTouchCirc extends NormalCircle {
    	
    	//private int greyscale = 200;
    	
		@Override
		public void init() {
			this.setARGB(0, 0, 255, 0);
			
			this.setRad(20F);
			
			super.init();
		}
    	
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
			
				this.circleAnim();
				this.drawCircleOnce(c);
			}
		}
		

		@Override
		public void circleAnim() {

			int cf = this.getCurrframe();

			if (!this.getRelAnim()) {

				if (cf < 600) {

					//this.alphaIncrement(12.4F, 225F);
					this.alphaIncrement(12.4F, 0F);
					// Log.d("circleAnim", "alpha val " + this.getAlpha());
					this.frameAdvance();
				}

				// else if (cf == 600) { // アニメーション終了
				// // this.setAlive(false);
				// }

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

    	
    }
    
/*	public class BlackFadeLayer extends NormalCircle {

		@Override
		public void init() {

			this.setARGB(255, 255, 255, 255);

			super.init();
		}

		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				this.fadeAnim();
				this.drawScreenBlack(c);
			}
		}

		public void drawScreenBlack(Canvas c) {
			c.drawARGB(this.getAlpha(), this.getRed(), this.getGrn(),
					this.getBlu());
		}

		public void fadeAnim() {

			int cf = this.getCurrframe();

			if (cf < 60) {
				this.alphaDecrement(8.7F, 0F);

				// Log.d("BlackFadeLayer", "this.getAlpha() " +
				// this.getAlpha());
				this.frameAdvance();

			}

			if (cf == 60) {
				this.setAlive(false);
			}

		}

	}*/
	
	
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

//			this.setRad(34F);
//			this.setPosX(70F);
//			this.setPosY((float) getHeight() - 76F);
//
//			this.setARGB(255, 200, 0, 0);

			// this.setYchgspd(-0.1F);

			// this.setRadfadeangle(0F);
			
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

				// this.alphaDecrement(1.5F, 0F);
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
			
			//path.
		}

		@Override
		public void circleAnim() {

			int cf = this.getCurrframe();

			if (cf < 45) {
				this.alphaIncrement(9.0F, 255F);
				// this.blinkFrame(7);

				// this.alphaDecrement(1.5F, 0F);
				this.frameAdvance();

			} else if (cf >= 45 && cf < 163) {

				// this.blinkFrame(7);

				this.alphaDecrement(4.5F, 20F);
				this.frameAdvance();
			} else if (cf == 163) { // アニメーション終了
				this.setAlive(false);
			}

		}

		@Override
		public void drawCircleOnce(Canvas c) {

			this.getPaint().setColor(
					Color.argb((this.getAlpha() 
							* this.getAlphaonoff()), 0, 0, 220));
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

	}
	
    public class AccelTouch extends NormalCircle {
    	
    	private PlainTouchCirc targetpoint1;
    	
    	private float maxspeedx = 45F;
    	private float maxspeedy = 45F;
    	
    	private float targetx;// = 0.0F;
    	private float targety;// = 0.0F;
    	    	
    	
    	//public AccelTouch
    	
		@Override
		public void init() {

//			this.setPosX((float)getWidth() - 300F);
//			this.setPosY((float)getHeight() - 200F);
			
			this.setPosX(this.targetpoint1.getPosX());
			this.setPosY(this.targetpoint1.getPosY());
			
			this.setYchgspd(0F);
			this.setXchgspd(0F);

//			this.setRad(30F);
//			this.setBaserad(this.getRad());
//
//			this.setARGB(0, 0, 255, 0);

			this.getPaint().setStyle(Paint.Style.FILL);
		
			super.init();
		}
		
		@Override
		public void circleAnim() {

			int cf = this.getCurrframe();

			if (!this.getRelAnim()) {
				if (cf < 600) {
					this.alphaIncrement(12.4F, 225F);
				}
			} else {
				if (cf < 150) {
					this.alphaDecrement(9.3F, 0F);
					this.radIncrement();
				} else if (cf == 150) {
					this.setAlive(false);
				}
			}
			this.frameAdvance();
			this.yIncrement();
			this.xIncrement();
		}
		
		
		
		
		
		
		
/*		@Override
		public void circleAnim() {
			


			this.yIncrement();
			this.xIncrement();
		}*/

		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				
				this.getCoordsFromTarget();
				this.xCalcSpeed();
				this.yCalcSpeed();
				this.circleAnim();
				this.drawCircleOnce(c);
			}
		}
		

		
		public void xCalcSpeed() {

			this.setXchgspd(((this.targetx - this.getPosX()) / getWidth()) * this.maxspeedx);
		}
		
		public void yCalcSpeed() {

			this.setYchgspd(((this.targety - this.getPosY()) / getWidth()) * this.maxspeedy);
		}


		
/*		protected float getTargetx() {
			return targetx;
		}
		
		protected float getTargety() {
			return targety;
		}
		
		protected void setTargetx(float targetx) {
			this.targetx = targetx;
		}
		
		protected void setTargety(float targety) {
			this.targety = targety;
		}*/
		
		protected void setTargetXy(float targetx, float targety) {
			this.targetx = targetx;
			this.targety = targety;
		}
		
		protected void getCoordsFromTarget() {
			if (this.targetpoint1 != null) {
				this.targetx = this.targetpoint1.getPosX();
				this.targety = this.targetpoint1.getPosY();
			}
		}
		
		protected PlainTouchCirc getTargetpoint1() {
			return targetpoint1;
		}
		protected void setTargetpoint1(PlainTouchCirc targetpoint1) {
			this.targetpoint1 = targetpoint1;
		}
		
		
    }
	
	

}


