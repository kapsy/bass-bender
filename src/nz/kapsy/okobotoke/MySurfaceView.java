package nz.kapsy.okobotoke;

import java.util.Random;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

public class MySurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {

	private Thread drawthread = null;
	private boolean isattached = false;
	private long timestr = 0, timefin = 0; //threadfin = 0;
	// 1000 / 28 = 35.714 fps
	// 1000 / 22 = 45.4545 fps
	private int threadinterval = 20; 
	
	private SurfaceHolder holder;
	
	private int bgcolor = Color.BLACK;
    private float maincirclesatcontrol = 1;

    public RainStar[] rainstars; 
    //public NormalCircle foggylight;  
    
    // counts total in array
    private int touchobjs = 3;
    // target like points. not drawn at all.
    // 的みたいな点。全く描いてない。
    public TargetTouch[] targtouchfirst;   
    private int curtargtouchfirst = 0;
    public TargetTouch[] targtouchsecond; 
    private int curtargtouchsecond = 0;
    
    
    public NormalLineFader[] faderline;
  //  private int curfaderline = 1;
    
    public AccelTouch[] acceltouchfirstrec;
    private int curacceltouchfirstrec = 1;
    
    public AccelTouch[] acceltouchfirstplay;
    private int curacceltouchfirstplay = 1;
    
    private AccelTouch initialcirclepointer;
    
    
    public AccelTouch[] acceltouchsecond;
  //  private int curpbacceltouchsecond = 1;
    
    
    
    public Circle2[] maincircles;
    private int curmaincircle = 0;
    public SonarCircle2 sonarcircle2;
    
    // fm rec
    public RecordBar recbar;
	public FrameRecorder framerec = new FrameRecorder();
	
	// sonar rec
    public RecordBarSonar recbarsonar;
	public FrameRecorder framerecsonar = new FrameRecorder();
	
	public RecSymbol recsymbol;
	public PlaySymbol playsymbol;
	public PlaySymbolCntr playsymbolcntr;
	
	// used for disabling touch once record has finished 
	// but user is still touching 
	// enabled once ACTION_UP is called

	// 記録後にタッチ操作を無効するためのフィルド
	// ACTION_UPが起きるとtrueに変わる。
//	private boolean touchenabledafterrec = true;
//	
	// general touch enable/disable field
	// 一般的なタッチ操作を有効／無効するためのフィールド
//	boolean firsttouchenabled = true;
//	boolean secondtouchenabled = true;
	//boolean actiondownwhiletouchdisabled = false;
	
	private boolean fmrecmode = true;
	private boolean touchenabled = true;
	
	
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
    	Log.d("doubletimeprob", "surfaceChanged");	
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
		//Log.d("Pd Debug", "surfaceDestroyed");
    	Log.d("doubletimeprob", "surfaceDestroyed");	
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// Log.d("Pd Debug", "surfaceCreated");
		Log.d("doubletimeprob", "surfaceCreated");
	}
    
	public void initDrawables() {

		if (this.isattached == false) {
//			int rectheight = (int) ((float) getWidth() * ((float) backg_bitmap
//					.getHeight() / (float) backg_bitmap.getWidth()));
			// bground = new BackGround();

			// screensizerect = new Rect(0, 0, getWidth(), rectheight);
			
			screenwidth = (float) this.getWidth();
			screenheight = (float) this.getHeight();
			screendiag = this.getScreenDiag();
//			Log.d("ScreenDims", 
//					"this.screenwidth " + this.screenwidth +  
//					"\n" + " this.screenheight " + this.screenheight + 
//					"\n" + " this.screendiag " + this.screendiag);
			

			maincircles = new Circle2[6];
			for (int i = 0; i < maincircles.length; i++) {
				maincircles[i] = new Circle2();
			}

			sonarcircle2 = new SonarCircle2();

			targtouchfirst = new TargetTouch[this.touchobjs];
			for (int i = 0; i < targtouchfirst.length; i++) {
				targtouchfirst[i] = new TargetTouch();
			}
			targtouchsecond = new TargetTouch[this.touchobjs];
			for (int i = 0; i < targtouchsecond.length; i++) {
				targtouchsecond[i] = new TargetTouch();
			}
			
			// 0: rec 1: pb 2: last point before death
			acceltouchfirstrec = new AccelTouch[this.touchobjs];
			for (int i = 0; i < acceltouchfirstrec.length; i++) {
				acceltouchfirstrec[i] = new AccelTouch();
				acceltouchfirstrec[i].setRad(this.percToPixX(6.25F));
				acceltouchfirstrec[i].setBaserad(acceltouchfirstrec[i].getRad());
				acceltouchfirstrec[i].setARGB(0, 0, 255, 0);
			}
			acceltouchfirstplay = new AccelTouch[this.touchobjs];
			for (int i = 0; i < acceltouchfirstplay.length; i++) {
				acceltouchfirstplay[i] = new AccelTouch();
				acceltouchfirstplay[i].setRad(this.percToPixX(6.25F));
				acceltouchfirstplay[i].setBaserad(acceltouchfirstplay[i].getRad());
				acceltouchfirstplay[i].setARGB(0, 0, 255, 0);
			}
			
			
			
			acceltouchsecond = new AccelTouch[this.touchobjs];
			for (int i = 0; i < acceltouchsecond.length; i++) {
				acceltouchsecond[i] = new AccelTouch();
				acceltouchsecond[i].setRad(this.percToPixX(4.166F));
				acceltouchsecond[i].setBaserad(acceltouchsecond[i].getRad());
				acceltouchsecond[i].setARGB(0, 0, 255, 0);
			}
			faderline = new NormalLineFader[this.touchobjs];
			for (int i = 0; i < faderline.length; i++) {
				faderline[i] = new NormalLineFader();
			}
			
			this.initialcirclepointer = new AccelTouch();
			initialcirclepointer.setPosX(this.percToPixX(35F));
			initialcirclepointer.setPosY(this.percToPixY(65F));
			
			
			rainstars = new RainStar[40];
			for (int i = 0; i < rainstars.length; i++) {
				rainstars[i] = new RainStar();
			}
			//12000
			recbar = new RecordBar(this.screenwidth, this.screenheight, 8000,
					threadinterval, this.framerec, this);
			recsymbol = new RecSymbol();
			playsymbol = new PlaySymbol();
			playsymbolcntr = new PlaySymbolCntr();
			//19000
			recbarsonar = new RecordBarSonar(this.screenwidth, this.screenheight, 3000,
					threadinterval, this.framerecsonar, this);
			
			this.isattached = true;
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
    
    public void stopThread() {
    	this.isattached = false;
    	this.drawthread = null;
    }

    // could use MotionEvent.Obtain for copying events
    // and dispatchTouchEvent(event) for playing back
    // listarray is faster though
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int pts = 0;
		int index = 99; // set to 99 as a non value
		

		if (this.fmrecmode && this.touchenabled) {	
			pts = event.getPointerCount();
			
			TargetTouch tt1 = this.targtouchfirst[this.getCurtargtouchfirst()];
			TargetTouch tt2 = this.targtouchsecond[this.getCurtargtouchsecond()];
			NormalLineFader fdr = this.faderline[0];
			AccelTouch at1 = this.acceltouchfirstrec[this.getCuracceltouchfirstrec()];
			AccelTouch at2 = this.acceltouchsecond[0];
	
			//fdr.setSnagpoint1(at1);
			//fdr.setSnagpoint2(at2);
			
			float x0 = event.getX(0);
			float y0 = event.getY(0);
			float x1 = 0F;
			float y1 = 0F;
	
			if (pts == 2) {
				x1 = event.getX(1);
				y1 = event.getY(1);
			}
	
			index = event.getActionIndex();
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
	
			case MotionEvent.ACTION_DOWN:
				Log.v("MotionEvent", "ACTION_DOWN, " + "index: " + index);
	
				// automatically starts recording if touched while playing
				if (this.framerec.isPlayingback()
						|| !this.framerec.isRecordingnow()) {
	
					this.framerec.startRecord();
					this.releaseAllTouchPlayAnims();
					this.recbar.init();
				}		
							
				if (!at1.isAlive()) {
	
					this.nextTargtouchfirst();
					tt1 = this.targtouchfirst[this.getCurtargtouchfirst()];
					tt1.setPosX(x0);
					tt1.setPosY(y0);
	
					at1 = this.acceltouchfirstrec[this.nextAcceltouchfirstrec()];
					at1.setTargetpoint1(tt1);
					at1.init();
	
					this.framerec.setFrameEssential(x0, y0, x1, y1,
							MotionEvent.ACTION_DOWN, pts, index);
				}
				break;
	
			case MotionEvent.ACTION_POINTER_DOWN:
				Log.v("MotionEvent", "ACTION_POINTER_DOWN, " + "index: " + index);
	
				if (at1.isAlive() && !at1.isPlayrelanim() 
						&& !at2.isAlive() && pts == MULTI_TOUCH_MAX) {
	
					this.nextTargtouchsecond();
					tt2 = this.targtouchsecond[this.getCurtargtouchsecond()];
					tt2.setPosX(x1);
					tt2.setPosY(y1);
	
					
					at2.setTargetpoint1(tt2);
					at2.init();
					
					fdr.setSnagpoint1(at1);
					fdr.setSnagpoint2(at2);
					fdr.init();
	
					this.framerec.setFrameEssential(x0, y0, x1, y1,
							MotionEvent.ACTION_POINTER_DOWN, pts, index);
				}
				break;
				
			case MotionEvent.ACTION_MOVE:
				Log.v("MotionEvent", "ACTION_MOVE, " + "index: " + index);
	
				if (at1.isAlive() && !at1.isPlayrelanim()) {
	
					tt1.setPosX(x0);
					tt1.setPosY(y0);
	
					this.sendSingleTouchVals(
							at1.getPosX(),
							at1.getPosY());
	
					this.framerec.setMotionevent(MotionEvent.ACTION_MOVE);
	
					if (at2.isAlive() && !at2.isPlayrelanim() && pts == MULTI_TOUCH_MAX) {
						tt2.setPosX(x1);
						tt2.setPosY(y1);
	
						float fdrdist = fdr.calcDistance();
	
						OkobotokeActivity.sendFloat("fm_index", OkobotokeActivity
								.calcToRangeFM(fdrdist, screendiag));
					}
				}
				break;
				
			case MotionEvent.ACTION_POINTER_UP:
				Log.v("MotionEvent", "ACTION_POINTER_UP, " + "index: " + index);
	
				if (at1.isAlive() && at2.isAlive()) {
	
					if (index == 1 && !at2.isPlayrelanim()) {
						OkobotokeActivity.sendFloat("fm_index", 12F);
						fdr.relAnimOn();
						at2.relAnimOn();
						framerec.setTouchpts(1);
					}
					if (index == 0 && !at1.isPlayrelanim()) {
						this.releaseAllTouchRecAnims();
						framerec.setTouchpts(0);
					}
					this.framerec.setFrameEssential(x0, y0, x1, y1,
							MotionEvent.ACTION_POINTER_UP, pts, index);
				}
				break;
	
			case MotionEvent.ACTION_UP:
				Log.v("MotionEvent", "ACTION_UP, " + "index: " + index);
	
				if (at1.isAlive() && !at1.isPlayrelanim() && index == 0) {
	
					at1.relAnimOn();
					this.framerec.setFrameEssential(x0, y0, x1, y1,
							MotionEvent.ACTION_UP, pts, index);
					framerec.setTouchpts(0);
				}
				break;
			}
	
			// 指三本処理
			if (pts > MULTI_TOUCH_MAX && at1.isAlive() && at2.isAlive()) {
	
				this.releaseAllTouchRecAnims();
			}
			framerec.setTouchpts(pts);	
			
		} else if (!this.fmrecmode && this.touchenabled) {
			
			float x0 = event.getX(0);
			float y0 = event.getY(0);
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:

			// automatically starts recording if touched while playing
			if (this.framerecsonar.isPlayingback()
					|| !this.framerecsonar.isRecordingnow()) {

				this.framerecsonar.startRecord();
				this.recbarsonar.init();
			}

			OkobotokeActivity.sendFloat("pulse_pan",
					OkobotokeActivity.calcToRangePulsePan(x0, this.getScreenwidth()));

			OkobotokeActivity.sendFloat("pulse_freq",
					OkobotokeActivity.calcToRangePulseFrq(y0, this.getScreenheight()));

			OkobotokeActivity.sendBang("pulse_bang");

			this.sonarcircle2.setPosX(x0);
			this.sonarcircle2.setPosY(y0);
			this.sonarcircle2.init();
 
			this.framerecsonar.setFrameEssential(x0, y0, 0F, 0F,
					MotionEvent.ACTION_DOWN, pts, index);
			break;

//			case MotionEvent.ACTION_POINTER_DOWN:
//				break;
//
//			case MotionEvent.ACTION_MOVE:
//				break;
//
//			case MotionEvent.ACTION_POINTER_UP:
//				break;
//
//			case MotionEvent.ACTION_UP:
//				break;
			}
		}
		return true;
	}

//	protected boolean isTouchenabledafterrec() {
//		return touchenabledafterrec;
//	}
//	protected void setTouchenabledafterrec(boolean touchenabled) {
//		this.touchenabledafterrec = touchenabled;
//	}

//	protected void callMotionUps() {
//		
//		MotionEvent m1 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_POINTER_UP, 0, 0, 0);
//		this.dispatchTouchEvent(m1);
//		
//		MotionEvent m2 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0, 0, 0);
//		this.dispatchTouchEvent(m2);
//	}
	
	protected void addActionUps() {

		AccelTouch at1 = this.acceltouchfirstrec[this.getCuracceltouchfirstrec()];
		AccelTouch at2 = this.acceltouchsecond[0];
		NormalLineFader fdr = this.faderline[0];

		if (at2.isAlive() && !at2.isPlayrelanim()) {
			OkobotokeActivity.sendFloat("fm_index", 12F);
			fdr.relAnimOn();
			at2.relAnimOn();

			framerec.setTouchpts(1);
			this.framerec.setFrameEssential(
					at1.getPosX(), at1.getPosY(),
					at2.getPosX(), at2.getPosY(),
					MotionEvent.ACTION_POINTER_UP, 1, 1);
		}
		if (at1.isAlive() && !at1.isPlayrelanim()) {
			at1.relAnimOn();

			framerec.setTouchpts(0);
			this.framerec.setFrameEssential(
					at1.getPosX(), at1.getPosY(),
					at2.getPosX(), at2.getPosY(), 
					MotionEvent.ACTION_UP, 0, 0);
		}
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
		//double w = (double) this.getWidth();
		//double h = (double) this.getHeight();
		float diag = (float) Math.sqrt(Math.pow(this.screenwidth, 2D) + Math.pow(this.screenheight, 2D));
		return diag;
	}
    
	// screen percentage to pixels
	public float percToPixX(float percent) {
		float pixx = (this.screenwidth / 100F) * percent;
	//	pixx = (float) Math.round(pixx);
		return pixx;
	}
	
	public float percToPixY(float percent) {
		float pixy = (this.screenheight / 100F) * percent;
		return pixy;
	}
	
	public float getMaincirclesatcontrol() {
		return maincirclesatcontrol;
	}

	public void setMaincirclesatcontrol(float maincirclesatcontrol) {
		this.maincirclesatcontrol = maincirclesatcontrol;
	}
	
	public void draw() {

		canvas = holder.lockCanvas();

		if (canvas != null) {

//			 Log.d("AccelTouch",
//					 "firsttouchenabled " + firsttouchenabled);
			//this.debugframecounter++;
			
//			Log.d("Snagcheck", "frame count: " + this.debugframecounter);
			canvas.drawColor(this.bgcolor);

			for (int i = 0; i < rainstars.length; i++) {
				rainstars[i].drawSequence(canvas);
			}
			
			
			


			for (int i = 0; i < maincircles.length; i++) {
				maincircles[i].drawSequence(canvas);
//						Log.d("circprob", 
//								"\n" + "maincircles[" + i + "] getTargetcircle1(): " +  maincircles[i].getTargetpointat()
//								+ "\n" + "maincircles[" + i + "] x: " +  maincircles[i].getAcceltargetx()
//								+ "\n" + "maincircles[" + i + "] y: " +  maincircles[i].getAcceltargety());
			
			}
			
			

			
			
			this.sonarcircle2.drawSequence(canvas);

			TargetTouch tt1 = this.targtouchfirst[this
					.getCurtargtouchfirst()];
			TargetTouch tt2 = this.targtouchsecond[this
					.getCurtargtouchsecond()];
			
			this.framerec.setFrameMovement(tt1.getPosX(), tt1.getPosY(),
					tt2.getPosX(), tt2.getPosY());
			
			this.framerecsonar.setFrameMovement(0F, 0F, 0F, 0F);
			
			
			
			
			this.playBackRecording();

			
			
			if (this.framerec.isRecordingnow()
					|| this.acceltouchfirstrec[this.getCuracceltouchfirstrec()].isAlive()) {
				//Log.d("drawingefficiency", "drawing while recording");
				for (int i = 0; i < acceltouchfirstrec.length; i++) {
					acceltouchfirstrec[i].drawSequence(canvas);
				}
			}
			
			if (this.framerec.isPlayingback()
					|| this.acceltouchfirstplay[this.getCuracceltouchfirstplay()].isAlive()) {
				//Log.d("drawingefficiency", "drawing while playing");
				for (int i = 0; i < acceltouchfirstplay.length; i++) {
					acceltouchfirstplay[i].drawSequence(canvas);
				}
			}
			
			
			
			
			
			
//			for (int i = 0; i < acceltouchfirstplay.length; i++) {
//				acceltouchfirstplay[i].drawSequence(canvas);
//
////				if (this.acceltouchfirst[i].isAlive()) {
////					Log.d("Snagcheck", 
////							"acceltouchfirst[" + i + "] coords: X: "
////							+ this.acceltouchfirst[i].getPosX() 
////							+ " Y: "
////							+ this.acceltouchfirst[i].getPosY());
////				}
//			}
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
			this.recbarsonar.drawSequence(canvas);
			
			holder.unlockCanvasAndPost(canvas);
		}
	}
    

	public void playBackRecording() {
		
		if (this.framerec.isPlayingback()) {
		TargetTouch tt1 = this.targtouchfirst[this.getCurtargtouchfirst()];
		TargetTouch tt2 = this.targtouchsecond[this.getCurtargtouchsecond()];
        NormalLineFader fdr = this.faderline[1];
    	AccelTouch at1 = this.acceltouchfirstplay[this.getCuracceltouchfirstplay()];
    	AccelTouch at2 = this.acceltouchsecond[1];
		
//		fdr.setSnagpoint1(at1);
//		fdr.setSnagpoint2(at2);
    	
			FrameRecUnit fru = this.framerec.getPlaybackFrame();
			
			switch (fru.getMotionevent()) {


			case MotionEvent.ACTION_DOWN:
//				Log.d("playbacktouch", "case MotionEvent.ACTION_DOWN:");
				
				this.nextTargtouchfirst();
				tt1 = this.targtouchfirst[this.getCurtargtouchfirst()];
				tt1.setPosX(fru.getCirtfirstx());
				tt1.setPosY(fru.getCirtfirsty());
				
			//	at1 = this.acceltouchfirst[this.nextPbAccelFirst()];
				at1 = this.acceltouchfirstplay[this.nextAcceltouchfirstplay()];
				at1.setTargetpoint1(tt1);
				at1.init();

				break;

			case MotionEvent.ACTION_POINTER_DOWN:
//				Log.d("playbacktouch", "case MotionEvent.ACTION_POINTER_DOWN:");

					this.nextTargtouchsecond();
					tt2 = this.targtouchsecond[this.getCurtargtouchsecond()];
					tt2.setPosX(fru.getCirtsecondx());
					tt2.setPosY(fru.getCirtsecondy());


				//	at2 = this.acceltouchsecond[this.nextPbAccelSecond()];
					at2.setTargetpoint1(tt2);
					at2.init();

				//	fdr = this.faderline[this.nextPbFaderLine()];
					fdr.setSnagpoint1(at1);
					fdr.setSnagpoint2(at2);
					fdr.init();

				break;

			case MotionEvent.ACTION_MOVE:
				// Log.d("playbacktouch", "case MotionEvent.ACTION_MOVE:");

					tt1.setPosX(fru.getCirtfirstx());
					tt1.setPosY(fru.getCirtfirsty());
					this.sendSingleTouchVals(at1.getPosX(), at1.getPosY());
					if (fru.getTouchpts() == 2) {

						tt2.setPosX(fru.getCirtsecondx());
						tt2.setPosY(fru.getCirtsecondy());

						OkobotokeActivity.sendFloat(
								"fm_index",
								OkobotokeActivity.calcToRangeFM(
										fdr.calcDistance(), screendiag));

						// float sat = OkobotokeActivity.calcToRangeSaturation(
						// fdr.calcDistance(),
						// this.screendiag);
						//
						// Log.d("saturation", "float sat: " + sat);
						//
						// this.setMaincirclesatcontrol(sat);
						// maincircles[this.getCurmaincircle()].faderSatToCirc(sat);
					}
				break;

			case MotionEvent.ACTION_POINTER_UP:
//				Log.d("playbacktouch", "case MotionEvent.ACTION_POINTER_UP:");
				
				if (fru.getIndex() == 0) {
					OkobotokeActivity.sendFloat("fm_index", 12F);
					this.releaseAllTouchPlayAnims();
				} else if (fru.getIndex() == 1) {
					OkobotokeActivity.sendFloat("fm_index", 12F);
					fdr.relAnimOn();
					at2.relAnimOn(); 
				}

				break;

			case MotionEvent.ACTION_UP:
//				Log.d("playbacktouch", "case MotionEvent.ACTION_UP:");
				
				this.releaseAllTouchPlayAnims();
				break;
			}

			// 指三本処理
			if (fru.getTouchpts() > MULTI_TOUCH_MAX) {
//				Log.d("playbacktouch", "if (fru.getTouchpts() > MULTI_TOUCH_MAX)");
				this.releaseAllTouchPlayAnims();
			}
		}
		
		
		if (this.framerecsonar.isPlayingback()) {

			FrameRecUnit fru = this.framerecsonar.getPlaybackFrame();

			switch (fru.getMotionevent()) {

			case MotionEvent.ACTION_DOWN:
				// Log.d("playbacktouch", "case MotionEvent.ACTION_DOWN:");

				OkobotokeActivity.sendFloat(
						"pulse_pan",
						OkobotokeActivity.calcToRangePulsePan(
								fru.getCirtfirstx(), this.getScreenwidth()));

				OkobotokeActivity.sendFloat(
						"pulse_freq",
						OkobotokeActivity.calcToRangePulseFrq(
								fru.getCirtfirsty(), this.getScreenheight()));

				OkobotokeActivity.sendBang("pulse_bang");

				this.sonarcircle2.setPosX(fru.getCirtfirstx());
				this.sonarcircle2.setPosY(fru.getCirtfirsty());
				this.sonarcircle2.init();
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				// Log.d("playbacktouch", "case MotionEvent.ACTION_POINTER_DOWN:");
				break;

			case MotionEvent.ACTION_MOVE:
				// Log.d("playbacktouch", "case MotionEvent.ACTION_MOVE:");
				break;

			case MotionEvent.ACTION_POINTER_UP:
				// Log.d("playbacktouch", "case MotionEvent.ACTION_POINTER_UP:");
				break;

			case MotionEvent.ACTION_UP:
				// Log.d("playbacktouch", "case MotionEvent.ACTION_UP:");
				break;
			}

			// 指三本処理
			if (fru.getTouchpts() > MULTI_TOUCH_MAX) {
				// Log.d("playbacktouch",
				// "if (fru.getTouchpts() > MULTI_TOUCH_MAX)");

			}
		}
	}

  	public void releaseAllTouchRecAnims() {

		// reset tgt to null...
		OkobotokeActivity.sendFloat("fm_index", 12F);
		this.faderline[0].relAnimOn();
		this.acceltouchfirstrec[this.getCuracceltouchfirstrec()].relAnimOn();
		this.acceltouchsecond[0].relAnimOn();
		
		
		
	}
  	
  	public void releaseAllTouchPlayAnims() {

		OkobotokeActivity.sendFloat("fm_index", 12F);
		this.faderline[1].relAnimOn();
		this.acceltouchfirstplay[this.getCuracceltouchfirstplay()].relAnimOn();
		this.acceltouchsecond[1].relAnimOn();
	}
//  	public void allTouchFirstRecTargetsNull() {
//  		for (int i = 0; i < acceltouchfirstrec.length; i++) {
//  			this.acceltouchfirstrec[i].setTargetpoint1null();
//  		}
//  	}
//	
//  	public void allTouchFirstPlayTargetsNull() {
//  		for (int i = 0; i < this.acceltouchfirstplay.length; i++) {
//  			this.acceltouchfirstplay[i].setTargetpoint1null();
//  		}
//  	}
  	



	public void sendSingleTouchVals(float x, float y) {
		OkobotokeActivity.sendFloat(
				"cntr_freq",
				OkobotokeActivity.calcToRangeCentFreq(y, this.screenheight));
		
		OkobotokeActivity.sendFloat(
				"bender",
				OkobotokeActivity.calcToRangeBender(x, this.screenwidth));
	}

	//cycle through object 配列
	public void nextCirc() {
		if(curmaincircle < maincircles.length - 1) {
			curmaincircle++;
		} else if (curmaincircle == maincircles.length - 1) {
			curmaincircle = 0;
		}
	}
	
	public void nextTargtouchfirst() {
		if(curtargtouchfirst < targtouchfirst.length - 1) {
			curtargtouchfirst++;
		} else if (curtargtouchfirst == targtouchfirst.length - 1) {
			curtargtouchfirst = 0;
		}
	}
    
	public void nextTargtouchsecond() {
		if(curtargtouchsecond < targtouchsecond.length - 1) {
			curtargtouchsecond++;
		} else if (curtargtouchsecond == targtouchsecond.length - 1) {
			curtargtouchsecond = 0;
		}
	}
	
	
	public int nextAcceltouchfirstrec() {
		if(curacceltouchfirstrec < acceltouchfirstrec.length - 1) {
			curacceltouchfirstrec++;
		} else if (curacceltouchfirstrec == acceltouchfirstrec.length - 1) {
			curacceltouchfirstrec = 0;
		}
		return this.curacceltouchfirstrec;
	}
	
	public int nextAcceltouchfirstplay() {
		if(curacceltouchfirstplay < acceltouchfirstplay.length - 1) {
			curacceltouchfirstplay++;
		} else if (curacceltouchfirstplay == acceltouchfirstplay.length - 1) {
			curacceltouchfirstplay = 0;
		}
		return this.curacceltouchfirstplay;
	}
	


	protected int getCuracceltouchfirstrec() {
		return curacceltouchfirstrec;
	}
	protected void setCuracceltouchfirstrec(int curacceltouchfirstrec) {
		this.curacceltouchfirstrec = curacceltouchfirstrec;
	}
	
	protected int getCuracceltouchfirstplay() {
		return curacceltouchfirstplay;
	}
	protected void setCuracceltouchfirstplay(int curacceltouchfirstplay) {
		this.curacceltouchfirstplay = curacceltouchfirstplay;
	}

//	public int nextPbFaderLine() {
//		if (this.curpbfaderline == 1) {
//			this.curpbfaderline = 2;
//		} else {
//			this.curpbfaderline = 1;
//		}
//		return this.curpbfaderline;
//	}
//	
		
    protected int getCurmaincircle() {
		return curmaincircle;
	}
        
    protected int getCurtargtouchfirst() {
		return curtargtouchfirst;
	}

	protected int getCurtargtouchsecond() {
		return curtargtouchsecond;
	}

//	protected int getCurpbfaderline() {
//		return curpbfaderline;
//	}
//	
//	protected int getCurpbacceltouchfirst() {
//		return curpbacceltouchfirst;
//	}
//
//	protected int getCurpbacceltouchsecond() {
//		return curpbacceltouchsecond;
//	}
	
//    protected boolean isFirsttouchenabled() {
//		return firsttouchenabled;
//	}
//
//	protected boolean isSecondtouchenabled() {
//		return secondtouchenabled;
//	}
//
//	protected void setFirsttouchenabled(boolean firsttouchenabled) {
//		this.firsttouchenabled = firsttouchenabled;
//	}
//
//	protected void setSecondtouchenabled(boolean secondtouchenabled) {
//		this.secondtouchenabled = secondtouchenabled;
//	}

	protected AccelTouch getInitialcirclepointer() {
		return initialcirclepointer;
	}

	protected boolean isFmrecmode() {
		return fmrecmode;
	}

	protected void setFmrecmode(boolean fmrecmode) {
		this.fmrecmode = fmrecmode;
	}

	protected boolean isTouchenabled() {
		return touchenabled;
	}

	protected void setTouchenabled(boolean touchenabled) {
		this.touchenabled = touchenabled;
	}

	// animatable class extensions
	public class Circle2 extends NormalCircle {
		
		private float[] hsv = {360F, 1F, 1F};
		// private float satfromfader = 1;
		private AccelTouch targetpointat;

    	private TailCircle[] tails = new TailCircle[2];
		
    	//private float radfactor;// = 0.9F;
    	float radrnginit;
    	
		public Circle2() {
			this.setAlive(false);
			this.getPaint().setStyle(Paint.Style.FILL);
			this.getPaint().setAntiAlias(false);
			this.getPaint().setDither(false);
			
    		for (int i = 0; i < tails.length; i++) {
    			tails[i] = new TailCircle();
    		}
		}
		
    	@Override
    	public void init() {
    		
			this.setStartPosRndOffset((int)percToPixX(45F));
    		
			this.setYchgspd(0F);
			this.setXchgspd(0F);
			this.setMaxaccelspeedx(percToPixX(1.875F));
			this.setMaxaccelspeedy(percToPixX(1.875F));
			
//    		this.setRad(percToPixX(17F));
//    		this.setBaserad(this.getRad());
    		
			this.setBaserad(percToPixX(14F));
    		this.setRad(percToPixX(28F));
    		
    	this.radrnginit = this.getRad() - this.getBaserad();
    		
    		
    		//this.radfactor = 0.98F;
    		
    		this.setAlpha(0);
    		this.hsv[1] = MySurfaceView.this.getMaincirclesatcontrol();

    		//this.setYchgspd(-0.3F);
    		
    		this.setLinearyaccelfactor(1.05F);
    		
//    		this.setAccelangle(0F);
//    		this.setRadfadeangle(0F);
    		
//    		this.radfad1 = 1F;
//    		this.radfad2 = 220F;
//    		this.radfad3 = 320F;
//    		
//    		this.radfad4 = percToPixX(52.08F);
    		
			float masx = this.getMaxaccelspeedx();
			float masy = this.getMaxaccelspeedy();
    		
			float masfactor = 0.5F;
			
    		for (int i = 0; i < tails.length; i++) {
    			
    			tails[i].setParentsettings(this);
    			tails[i].setTargetcircle1(this.targetpointat);
    			
    			tails[i].init();
    			
    			masfactor = masfactor + 0.1F;
    			masx = masx * masfactor;
    			masy = masy * masfactor;
    			tails[i].setMaxaccelspeedx(masx);
    			tails[i].setMaxaccelspeedy(masy);
    			
    			tails[i].setRad(this.getRad());
    			tails[i].setPosX(this.getPosX());
    			tails[i].setPosY(this.getPosY());
    		}
    		
    		
    		super.init();
    	}
    	
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				this.getCoordsFromTarget();
//				if (!this.getRelAnim()) {
					this.xCalcSpeed((float) getWidth());
					this.yCalcSpeed((float) getWidth());
//				} else {
//				}
				this.circleAnim();
				this.calcHSVFromPos();
				// 今は必要ないかも
				// this.rampSatToFader();
				// this.drawCircleFadedEdges(1, 21F, 20, c);
				//this.radFadeLinIn();
				this.decelRadFade(1F, -0.08F);
				
				
				float a = (float) this.getAlpha();
				float r = this.getRad();
				
				//int col;
				
				for (int i = 0; i < tails.length; i++) {
	    			a = a * 0.6F;
	    			r = r * 0.6F;

					tails[i].setRad(r);
					tails[i].getCoordsFromTargetCircle(); 
					tails[i].getPaint().setColor(Color.HSVToColor((int) a,	this.getHsv()));
	    			
//	    			Log.d("tails", "tails[" + i + "] x: " + tails[i].getPosX()
//	    					+ "\n" + "tails[" + i + "] y: " + tails[i].getPosY()
//	    					+ "\n" + "tails[i].getTargetpoint1(): " + tails[i].getTargetpoint1()
//	    					+ "\n" + "tails[i].getMaxaccelspeedx(): " + tails[i].getMaxaccelspeedx()
//	    					+ "\n" + "tails[i].getMaxaccelspeedy(): " + tails[i].getMaxaccelspeedy()
//	    					+ "\n" + "tails[i].isAlive() " + tails[i].isAlive()
//	    					+ "\n" + "tails[i].getAcceltargetx() " + tails[i].getAcceltargetx()
//	    					+ "\n" + "tails[i].getAcceltargety() " + tails[i].getAcceltargety()
//	    					+ "\n" + "tails[i].getXchgspd(): " + tails[i].getXchgspd()
//	    					+ "\n" + "tails[i].getYchgspd(): " + tails[i].getYchgspd());
				}
				
				for (int i = tails.length - 1; i >= 0;  i--) {
					tails[i].drawSequenceNoColor(c);
				}				
				
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
					this.alphaIncrement(7.2F, 160F);
				}
			} else {
				if (cf < 150) {
					this.alphaDecrement(3.5F, 0F);
					this.decelRadFade(1F, 0.17F);
					
					
				} else if (cf == 150) {
					this.setAlive(false);
				}
			}
			//this.radFade(this.radfad1, this.radfad2, this.radfad3, this.radfad4);
			this.yIncrement();
			this.xIncrement();
			this.frameAdvance();
		}

		private void calcHSVFromPos() {
			int m = (int)(this.getPosY() * (120F/(float)getScreenheight()));
			this.hsv[0] = 360 - m;
		}
		
		protected AccelTouch getTargetpointat() {
			return this.targetpointat;
		}

		protected float[] getHsv() {
			return hsv;
		}
		
		
		protected void setTargetpointat(AccelTouch targetpointat) {
			this.targetpointat = targetpointat;
			
    		for (int i = 0; i < tails.length; i++) {
    			tails[i].setParentsettings(this);
    			tails[i].setTargetcircle1(targetpointat);
    		}
		}

		protected void setTargetpointatnull() {
			this.targetpointat = null;
		}
		
		
		protected void getCoordsFromTarget() {
			if (this.targetpointat != null) {
				this.setTargetXy(this.targetpointat.getPosX(),
						this.targetpointat.getPosY());
			}
			
			//Log.d("targetpoint1", "this.targetpoint1.equals(null): " + this.targetpoint1.equals(null));
		}

		protected void setStartPosRndOffset(int rndmagnitude) {
			
			if (this.targetpointat != null) {
				
				// この値以上距離は必ず０になります
				// offset is always zero if AccelTouch is above this val
				float maxchgspd = 6F;
				
				float xspd = Math.abs(this.targetpointat.getXchgspd());
				float yspd = Math.abs(this.targetpointat.getYchgspd());
				float spd = xspd > yspd ? xspd : yspd ;
				//Log.d("startpos", "spd " + spd);
				
				float rnddist = 0;
				if (spd <= maxchgspd) {
					rnddist = ((spd * (-1F / maxchgspd )) + 1) * (float) rndmagnitude;
					//Log.d("startpos", "rnddist " + rnddist);
				}
				
				
				float x = 0F;
				float y = 0F;
				//float x = (rnd.nextFloat() * (rnddist * 2)) - rnddist;
				//float y = (rnd.nextFloat() * (rnddist * 2)) - rnddist;	
			
				x = (rnd.nextFloat() * rnddist * (rnd.nextBoolean() ? 1F : -1F));
				
				if (Math.abs(x) < (rnddist / 2F)) {
					y = ((0.5F + (rnd.nextFloat()/2F)) * rnddist * (rnd.nextBoolean() ? 1F : -1F));
				} else {
					y = (rnd.nextFloat() * rnddist * (rnd.nextBoolean() ? 1F : -1F));
				}
	
				//Log.d("startpos", " x: " + x + " y: " + y);
				
				this.setPosX(this.targetpointat.getPosX() + x);
				this.setPosY(this.targetpointat.getPosY() + y);
			}
		}

//		protected void radFadeLinIn() {
//			
//			
//			if (this.getRad() > this.getBaserad()) {
//				
//				float r = this.getRad() * this.radfactor;
//				
//				
//						 this.setRad(r);
////						 this.radfactor = (this.radfactor * 0.9F);
////						 
//						 Log.d("radfade", "this.getRad() " + this.getRad() + " this.getBaserad() " + this.getBaserad());
//			} else {
//				this.setRad(this.getBaserad());
//			}
//
//		}
		
		protected void decelRadFade(float decelmin, float decelrng) {
			
			float factor = 1;
			
		
			
			float radcurval = this.getRad() - this.getBaserad();
			
			factor = (radcurval * (decelrng/this.radrnginit)) + decelmin;
			
			// Log.d("radfade", "factor: " + factor);
			
			this.setRad(this.getRad() * factor);
			
			
		}
		
		
		
		protected TailCircle[] getTails() {
			return tails;
		}
	}
    
    
    public class SonarCircle2 extends NormalCircle {
    	
    	public SonarCircle2() {

    		this.setAlive(false);
    		this.getPaint().setStyle(Paint.Style.STROKE);
    		// paint.setStrokeWidth((float)(150 - rnd.nextInt(100)));
    		this.getPaint().setAntiAlias(false);
    		this.getPaint().setDither(false);
    	}
    	
    	@Override
    	public void init() {
//    		this.setPosX((float)rnd.nextInt(getWidth()));
//    		this.setPosY((float)rnd.nextInt(getHeight()));
    		this.setARGB(160, 255 - rnd.nextInt(30), 0, 0);
    		    		
    		this.setRad(percToPixX(4.166F));
    		this.setRadchgspd(percToPixX(10.41F));
    		// to int and then float again to lock off remainder	
    		this.getPaint().setStrokeWidth((float) Math.round(percToPixX(4F + (rnd.nextFloat() * 7F))));
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
	    	if (cf < 25){
	    		this.radIncrement();
    			this.frameAdvance();
	    	}
	    	else if (cf == 25) { 
	    		this.setAlive(false);
	    	}
    	}
    }
    public class NormalLineFader extends NormalLine {

    	private boolean startfadeout = false;
    	
    	@Override
    	public void init() {
//    		this.setARGB(0, 0, 255, 0);
    		this.getPaint().setStyle(Paint.Style.STROKE);
    		//this.getPaint().setStrokeWidth(4F);
    		this.getPaint().setStrokeWidth(percToPixX(0.8333F));
    		
			if (MySurfaceView.this.framerec.isRecordingnow()) {
				this.setRGB(0, 255, 0);
				
			} else if (MySurfaceView.this.framerec.isPlayingback()) {
				this.setRGB(0, 0, 255);
			}
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
		
		@Override
		public void circleAnim() {
			// Log.d("AccelTouch", "this.getAlpha() " + this.getAlpha());

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
		}
    }
    
    
    // ここまでパーセント方式に変化すること
    
	public class RainStar extends NormalCircle {

		private float radmodspd;
		private float radmodamp;

		private float acctargetspd;

		@Override
		public void init() {
			this.setPosX((float) rnd.nextInt(getWidth()));
			this.setPosY((float) rnd.nextInt(getHeight()));
			
			this.setYchgspd(percToPixX(0.0208F));
						
			this.setAccelangle(0F);
						
			this.setRad(percToPixX(1.041F + (rnd.nextFloat() * 4.166F)));
						
			this.setBaserad(this.getRad());
						
			this.radmodspd = percToPixX(rnd.nextFloat() * 0.5208F);

			// ensures that modulation is between .1 and .25 the total radius
			// 多分、時間によってこの値を十倍までに増やせばいいんじゃない
			this.radmodamp = this.getRad()
					* ((float) (10 + rnd.nextInt(25)) / 100F);
			
			this.acctargetspd = percToPixX(rnd.nextFloat() * 0.4166F);	
			
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
    
	// not drawn, just a pointer for AccelTouch
	public class TargetTouch {
		private float posx;
		private float posy;
		
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
			this.getPaint().setColor(
					Color.argb((this.getAlpha() * alphaonoff), 220, 0, 0));

			// c.drawCircle(getScreenwidth() / 2F, getScreenheight() / 2F,
			// percToPixX(9F), this.getPaint());

			// c.drawCircle(percToPixX(20F), percToPixY(20F) / 2F,
			// percToPixX(8F), this.getPaint());

			c.drawCircle(percToPixX(15F), getScreenheight() - percToPixY(10F),
					percToPixX(7F), this.getPaint());

			// PathMeasure p = new PathMeasure(path, forceClosed);
		}
		
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				this.circleAnim();
		
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
			this.getPaint().setAntiAlias(true);
			this.path = new Path();

			points[0] = new PointF(percToPixX(8F),
					(getScreenheight() - percToPixY(16F)));
			points[1] = new PointF(percToPixX(8F),
					(getScreenheight() - percToPixY(6F)));
			points[2] = new PointF(percToPixX(20F),
					(getScreenheight() - percToPixY(11F)));
						
			path.moveTo(points[0].x, points[0].y);
			path.lineTo(points[1].x, points[1].y);
			path.lineTo(points[2].x, points[2].y);
		}

		@Override
		public void circleAnim() {
			// quick and dirty touch disable while drawing

			MySurfaceView.this.setTouchenabled(false);
			// if (MySurfaceView.this.recsymbol.isAlive())
			int cf = this.getCurrframe();
			if (cf < 45) {
				this.alphaIncrement(9.0F, 255F);
			} else if (cf >= 45 && cf < 110) {
				this.alphaDecrement(4.5F, 30F);
			} else if (cf == 110) { // アニメーション終了
				this.setAlive(false);

				MySurfaceView.this.setTouchenabled(true);
			}
			this.frameAdvance();

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
		
		@Override
		public void circleAnim() {
			// quick and dirty touch disable while play is drawing
			MySurfaceView.this.setTouchenabled(false);

			int cf = this.getCurrframe();

			if (cf < 45) {
				this.alphaIncrement(9.0F, 255F);

			} else if (cf >= 45 && cf < 163) {

				this.alphaDecrement(4.5F, 30F);
			} else if (cf == 163) { // アニメーション終了
				MySurfaceView.this.setTouchenabled(true);
				this.setAlive(false);
			}
			this.frameAdvance();
		}
		
		
		public PlaySymbolCntr() {

			this.getPaint().setStyle(Paint.Style.FILL);
			this.getPaint().setAntiAlias(true);

			Path path = this.getPath();
			PointF[] points = this.getPoints();

			float cntrx = getScreenwidth() / 2;
			float cntry = getScreenheight() / 2;
			
			points[0].set(cntrx - percToPixX(6F), cntry - percToPixY(7F));
			points[1].set(cntrx - percToPixX(6F), cntry + percToPixY(7F));
			points[2].set(cntrx + percToPixX(14F), cntry);
			
			path.reset();
			
			path.moveTo(points[0].x, points[0].y);
			path.lineTo(points[1].x, points[1].y);
			path.lineTo(points[2].x, points[2].y);
		}
		
	}
	
    public class AccelTouch extends NormalCircle {
    	
    	
    	
    	private boolean startfadeout = false;
//    	private boolean firsttouchenabledcalled = false;
//    	private boolean initwhilerecording = true;
    	
    	private int relframecounter = 0;
    	
    	private TailCircle[] tails = new TailCircle[6];
    	
    	
    	public AccelTouch() {
    		
    		this.setAlive(false);

    		this.getPaint().setStyle(Paint.Style.FILL);
    		this.getPaint().setAntiAlias(false);
    		this.getPaint().setDither(false);
    	}
    	
    	
    	
    	
		@Override
		public void init() {

			super.init();
			
			this.setMaxaccelspeedx(percToPixX(9.5F));
			this.setMaxaccelspeedy(percToPixX(9.5F));
			
			this.setPosX(this.getTargetpoint1().getPosX() + percToPixX((rnd.nextFloat() * 12F) - 24F));
			this.setPosY(this.getTargetpoint1().getPosY() + percToPixX((rnd.nextFloat() * 12F) - 24F));
			
			this.setYchgspd(0F);
			this.setXchgspd(0F);

			this.getPaint().setStyle(Paint.Style.FILL);

			if (MySurfaceView.this.framerec.isRecordingnow()) {
				//this.initwhilerecording = true;
				this.setRGB(0, 255, 0);
				
			} else if (MySurfaceView.this.framerec.isPlayingback()) {
				//this.initwhilerecording = false;
				this.setRGB(0, 0, 255);
			}
		//	 Log.d("AccelTouch", "initwhilerecording: " + initwhilerecording);
			
			
			float masx = this.getMaxaccelspeedx();
			float masy = this.getMaxaccelspeedy();
			
			float masfactor = 0.60F;
			
			// この辺を見なおすべき
    		for (int i = 0; i < tails.length; i++) {
    			tails[i] = new TailCircle();

    			tails[i].setParentsettings(this);
    			tails[i].setTargetpoint1(this.getTargetpoint1());
    			
//    			masx = masx * 0.8F;
//    			masy = masy * 0.8F;
    			
    			//masfactor = masfactor + 0.055F;
    			masfactor = masfactor * 1.05F;
    			
    			masx = masx * masfactor;
    			masy = masy * masfactor;

    			tails[i].setMaxaccelspeedx(masx);
    			tails[i].setMaxaccelspeedy(masy);
    			
//    			Log.d("tails", 
//    					"\n" + "this.getMaxaccelspeedx(): " + this.getMaxaccelspeedx() 
//    					+ "\n" + "this.getMaxaccelspeedy(): " + this.getMaxaccelspeedy()
//    					+ "\n" + "tails[" + i + "].getMaxaccelspeedx(): " + tails[i].getMaxaccelspeedx()
//    					+ "\n" + "tails[" + i + "].getMaxaccelspeedy(): " + tails[i].getMaxaccelspeedy());
    			
    		}
			
    		for (int i = 0; i < tails.length; i++) {
    			tails[i].init();
    			tails[i].setRad(this.getRad());
    			tails[i].setPosX(this.getPosX());
    			tails[i].setPosY(this.getPosY());
    		}
    		
    		
//    		this.relframecounter = 0;
		}
		
		@Override
		public void circleAnim() {
			// Log.d("AccelTouch",
			// "firsttouchenabled " + firsttouchenabled);

			if (!this.getRelAnim()) {
				this.alphaIncrement(12.4F, 225F);
			} else {
//				this.relframecounter++;
//				Log.d("releaseframes", "count: " + this.relframecounter);
				
				
				if (!this.isStartfadeout()) {
					if (this.getAlpha() < 225) {
						this.alphaIncrement(12.4F, 225F);
					} else {
						this.setStartfadeout(true);
					}
				}
				if (this.isStartfadeout()) {
					this.alphaDecrement(9.3F, 0F);

					if (this.getAlpha() == 0) {
						this.setAlive(false);
						this.setStartfadeout(false);

//						MySurfaceView.this.acceltouchfirst[2].setPosX(this
//								.getPosX());
//						MySurfaceView.this.acceltouchfirst[2].setPosY(this
//								.getPosY());
//
//						for (int i = 0; i < MySurfaceView.this.maincircles.length; i++) {
//							
//							MySurfaceView.this.maincircles[i]
//									.setTargetpointat(MySurfaceView.this.acceltouchfirst[2]);
//							
//							for (int a = 0; a < MySurfaceView.this.maincircles[i]
//									.getTails().length; a++) {
//								
//								MySurfaceView.this.maincircles[i].getTails()[a]
//										.setTargetcircle1(MySurfaceView.this.acceltouchfirst[2]);
//							}
//						}
					}
				}
			}
			this.yIncrement();
			this.xIncrement();
		}
		
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {
				
				this.getCoordsFromTarget();
				this.xCalcSpeed((float) getScreenwidth());
				this.yCalcSpeed((float) getScreenwidth());
				this.circleAnim();			
				

				float a = (float) this.getAlpha();
				float r = this.getRad();
				
				for (int i = 0; i < tails.length; i++) {
	    			a = a * 0.65F;
	    			r = r * 0.80F;

	    		//tails[i].setAlpha((int) a);
	    			
	    			
					tails[i].setRad(r);
					tails[i].getCoordsFromTarget();
					// requires a as goes straight to paint
					// 直接的にPaintに伝えるので、aが必要
					tails[i].getRgbFromParentToColor((int) a);
					
					//tails[i].getRgbFromParent();
	    		//	tails[i].drawSequence(c);
	    			
/*	    			Log.d("tails", "tails[" + i + "] x: " + tails[i].getPosX()
	    					+ "\n" + "tails[" + i + "] y: " + tails[i].getPosY()
	    					+ "\n" + "tails[i].getTargetpoint1(): " + tails[i].getTargetpoint1()
	    					+ "\n" + "tails[i].getMaxaccelspeedx(): " + tails[i].getMaxaccelspeedx()
	    					+ "\n" + "tails[i].getMaxaccelspeedy(): " + tails[i].getMaxaccelspeedy()
	    					+ "\n" + "tails[i].isAlive() " + tails[i].isAlive()
	    					+ "\n" + "tails[i].getAcceltargetx() " + tails[i].getAcceltargetx()
	    					+ "\n" + "tails[i].getAcceltargety() " + tails[i].getAcceltargety()
	    					+ "\n" + "tails[i].getXchgspd(): " + tails[i].getXchgspd()
	    					+ "\n" + "tails[i].getYchgspd(): " + tails[i].getYchgspd());*/
	    			
				}
				// 一番小さい円形は一番下に逆の順番に描く
				// drawn in reverse so that smallest is at bottom
				for (int i = tails.length - 1; i >= 0;  i--) {
					tails[i].drawSequenceNoColor(c);
				}	
				this.drawCircleOnce(c);	
			}
		}
		
		protected boolean isStartfadeout() {
			return startfadeout;
		}

//		protected boolean isFirsttouchenabledcalled() {
//			return firsttouchenabledcalled;
//		}
//
//		protected boolean isInitwhilerecording() {
//			return initwhilerecording;
//		}
//
		protected void setStartfadeout(boolean startfadeout) {
			this.startfadeout = startfadeout;
		}
//
//		protected void setFirsttouchenabledcalled(boolean firsttouchenabledcalled) {
//			this.firsttouchenabledcalled = firsttouchenabledcalled;
//		}
//
//		protected void setInitwhilerecording(boolean initwhilerecording) {
//			this.initwhilerecording = initwhilerecording;
//		}
    }
    
//    public class AccelTouchSecond extends AccelTouch {
//    	
//		@Override
//		public void circleAnim() {
//
//			if (!this.getRelAnim()) {
//				this.alphaIncrement(12.4F, 225F);
//			} else {
//
//				if (!this.isFirsttouchenabledcalled()) {
//					if (this.isInitwhilerecording()) {
//						MySurfaceView.this.setSecondtouchenabled(false);
//					}
//					this.setFirsttouchenabledcalled(true);
//				}
//
//				if (!this.isStartfadeout()) {
//					
//					if (this.getAlpha() < 225) {
//						this.alphaIncrement(12.4F, 225F);
//					} else {
//						this.setStartfadeout(true);
//					}
//				}
//				if (this.isStartfadeout()) {
//					this.alphaDecrement(9.3F, 0F);
//					
//					if (this.getAlpha() == 0) {
//						this.setAlive(false);
//						// Log.d("AccelTouch",
//						// "Alpha 0, this.setAlive(false) called.");
//						
//						// this.isFirsttouchenabled()は偽なら、setSecondtouchenabled(true)を呼ばない
//						if (isInitwhilerecording() && MySurfaceView.this.isFirsttouchenabled()) {
//							MySurfaceView.this.setSecondtouchenabled(true);
//						}
//						this.setFirsttouchenabledcalled(false);
//						this.setStartfadeout(false);
//					}
//				}
//			}
//			this.yIncrement();
//			this.xIncrement();
//		}
//    }
    
    public class TailCircle extends NormalCircle {
    	
		private NormalCircle parentsettings;
    	
    	@Override
    	public void init() {
			//this.setStartPosRndOffset((int)percToPixX(20F));
			this.setYchgspd(0F);
			this.setXchgspd(0F);
    		this.setAlpha(0);
    		this.setLinearyaccelfactor(1.05F);
    		super.init();
    	}
	
		@Override
		public void drawSequence(Canvas c) {
			if (this.isAlive()) {

				// this.setRad(partgt.getRad() * 0.9F);

//				this.setRGB(parentsettings.getRed(),
//						parentsettings.getGrn(), parentsettings.getBlu());

				//this.getCoordsFromTarget();	    		
				this.xCalcSpeed((float) getScreenwidth());
				this.yCalcSpeed((float) getScreenwidth());
				
				this.circleAnim();
				this.drawCircleOnce(c);
			}
		}
				
		public void drawSequenceNoColor(Canvas c) {
			if (this.isAlive()) {
				
				this.xCalcSpeed((float) getScreenwidth());
				this.yCalcSpeed((float) getScreenwidth());
				
				this.circleAnim();
				this.drawCircleOnceNoColor(c);
			}
		}

		@Override
		public void circleAnim() {

			this.yIncrement();
			this.xIncrement();
		}
		
		public void getRgbFromParent() {
			this.setRGB(parentsettings.getRed(), parentsettings.getGrn(),
					parentsettings.getBlu());
		}
		
		public void getRgbFromParentToColor(int alpha) {
			this.getPaint().setColor(
					Color.argb(alpha, parentsettings.getRed(),
							parentsettings.getGrn(), parentsettings.getBlu()));
		}
		
		protected NormalCircle getParentsettings() {
			return parentsettings;
		}

		protected void setParentsettings(NormalCircle parentsettings) {
			this.parentsettings = parentsettings;
		}
		
    	
    	
    }
    
    
}