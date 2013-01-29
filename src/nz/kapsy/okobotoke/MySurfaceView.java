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

    private SurfaceHolder holder;
    
    
    //private OkobotokeActivity okoboactivity = (OkobotokeActivity)this.getContext();
    
   // public OkobotokeActivity okoboactivity = (OkobotokeActivity)this.getParent(); 
    //test1234sdafzvzd1123412341234testdsf
    
    //public Circle2 circle2;
    
    public Circle2[] maincircles;
    private int curmaincircle = 0;
    
            
    public NormalCircle twotouch1;    
    public NormalCircleMultiTouch testxtend;
    public NormalLineFader faderline;
    
    public SonarCircle2 sonarcircle2;
    	
	private boolean initbackground;

	Rect screensizerect;
	public Canvas canvas;
	    
	Bitmap backg_bitmap = 
    		BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.bg_test_2);
	
    private Random rnd = new Random();
    
    // タッチ処理
    final static int MULTI_TOUCH_MAX = 2;
    private PointF [] touchpoints = new PointF[MULTI_TOUCH_MAX];
    
    int oneintwo = 0;
    int oneinfour = 0;
    
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
        holder = getHolder();
        holder.setFormat(PixelFormat.OPAQUE);
        holder.addCallback(this);
        
        setFocusable(true);
        //requestFocus();
        
        for (int i = 0; i < MULTI_TOUCH_MAX; i++) {
        	touchpoints[i] = new PointF(-1.0F, -1.0F);
        }
        
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
            int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	
//        int h = backg_bitmap.getHeight();
//        int w = backg_bitmap.getWidth();
//        
//       
//float ratio = (float)backg_bitmap.getHeight() / (float)backg_bitmap.getWidth();
    	
    	// creates rectangle at ratio of source bitmap to fit screen
       int rectheight = (int)((float)getWidth()*((float)backg_bitmap.getHeight() / (float)backg_bitmap.getWidth()));
    	
       // new Rect(l, t, r, b);
		screensizerect = new Rect(0, 0, getWidth(), rectheight);
		   
		screendiag = this.getScreenDiag();
		screenwidth = (float)this.getWidth();
		screenheight = (float)this.getHeight(); 
               
        //circle2 = new Circle2();
        
		maincircles = new Circle2[6];
        for(int i = 0; i < maincircles.length; i++) {
        	maincircles[i] = new Circle2();
        }
        
        sonarcircle2 = new SonarCircle2();
                
        faderline = new NormalLineFader();
        twotouch1 = new NormalCircle();
        testxtend = new NormalCircleMultiTouch();
           
        initbackground = true;
                
        startnow(); // ★追加

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	
    	// 録音/再生機能を追加したいならここで一番いい
    	// if playingBack() {
    	// 		y = playback.getY();
    	// }
    	// else {
    	// 		y = event.getY(0); など
    	
    	// いえいえ全く違います・ACTION_DOWN処理とか・別のメソッドが必要 jgjkh
    	
    	int pts = event.getPointerCount();
    	
    	int index = event.getActionIndex();
//    	Log.v("MotionEvent", "event.getActionIndex() " + event.getActionIndex());

		 switch (event.getAction() & MotionEvent.ACTION_MASK) {
		 
		 	case MotionEvent.ACTION_DOWN:
//	 	        	Log.v("MotionEvent", "ACTION_DOWN");
    			 
   	    		this.testxtend.setPosX(event.getX(0));
 	    		this.testxtend.setPosY(event.getY(0));
 	    		
 	        	this.testxtend.init(0, rndCol(130), rndCol(130), rndCol(130));
 	        	this.testxtend.setRad(80F);
 	        	break;
			 
	 
	        case MotionEvent.ACTION_POINTER_DOWN:
//    	        	Log.v("MotionEvent", "ACTION_POINTER_DOWN");
	        	// ここで必要ないかも
	    		if (this.testxtend.getRelAnim() == false) {  	
	        	this.faderline.setLinePoints
    			(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
			    	    			
    			this.twotouch1.setPosX(event.getX(1));
	    		this.twotouch1.setPosY(event.getY(1));
	    	   
	    		this.faderline.init();	
	    	    
	    		this.twotouch1.init(7, rndCol(130), rndCol(130), rndCol(130));
	        	this.twotouch1.setRad(80F);
	    		}
	    		break;
	        	
	    	case MotionEvent.ACTION_MOVE:
//    	    		Log.v("MotionEvent", "ACTION_MOVE");
	    		
	    		if (this.testxtend.getRelAnim() == false) {
	    			this.testxtend.setPosX(event.getX(0));
    	    		this.testxtend.setPosY(event.getY(0));
	    	    	    			
    	    		OkobotokeActivity.sendFloat("cntr_freq", 
    	    				OkobotokeActivity.calcToRangeCentFreq(event.getY(0), this.screenheight));
    	    			

	    		
//	    	    			Log.d("sendFloat", "cntr_freq " + "test " + "testxtend.getPosY() " + testxtend.getPosY() 
//	    	    					+ " screenheight " + screenheight + " calcToRangeCentFreq " 
//	    	    					+ OkobotokeActivity.calcToRangeCentFreq(event.getY(0), this.screenheight));
//		
	    	
	    		if (pts == MULTI_TOUCH_MAX) {
	    			if (oneInTwo()) {
    	    			
    	    			this.faderline.setLinePoints
	    	    			(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    	    		
    	    			this.twotouch1.setPosX(event.getX(1));
	    	    		this.twotouch1.setPosY(event.getY(1));
	    	    				    	    		
	    	    		if (oneInFour()) {
	    	    			
	    	    			OkobotokeActivity.sendFloat("fm_index", OkobotokeActivity.calcToRangeFM(
	    	    			this.faderline.calcDistance(), screendiag));
	    	    			
//	    	    			Log.d("sendFloat", "fm_index " + "faderline.calcDistance() " 
//    	    					+ this.faderline.calcDistance() + " screendiag " + screendiag + " calcToRangeFM " + OkobotokeActivity.calcToRangeFM(
//    	    							this.faderline.calcDistance(), screendiag)); 
	    	    		}
    	    		}
	    		}
	    		}	    		    	    		
	    		break;
    		
	    	case MotionEvent.ACTION_POINTER_UP:
	    		// フェードアウト・アニメーション
//    	    		Log.v("MotionEvent", "ACTION_POINTER_UP");

	    		if (index == 1) {
	    			
		    		OkobotokeActivity.sendFloat("fm_index", 12F);
	    	    	this.twotouch1.setAlive(false);
	    	    	this.faderline.setAlive(false);
	    		}
	    		
   	    		if (index == 0) {
   	    			
       	 			OkobotokeActivity.sendFloat("fm_index", 12F);
       				this.twotouch1.setAlive(false);
       				this.testxtend.relAnimOn();
       				this.faderline.setAlive(false);
	    		}
	    		
	    		break;
	    		
	    	case MotionEvent.ACTION_UP:
//    	    		Log.v("MotionEvent", "ACTION_UP");
	    		
	    		if (index == 0) {
	    			this.testxtend.relAnimOn();
	    		}
	    			    	    		
	    		break;

		 }
    		 
    	if (pts > MULTI_TOUCH_MAX) {
    		
			OkobotokeActivity.sendFloat("fm_index", 12F);
			this.twotouch1.setAlive(false);
			this.testxtend.relAnimOn();
			
			this.faderline.setAlive(false);
    	}
    	
    	
//		invalidate();
		return true;
        //return super.onTouchEvent(event);
    }
    
 

    public boolean oneInTwo() {
    	
    	if (oneintwo == 2) {
    		oneintwo = 0;
    	}
    	if (oneintwo == 0) {
    		//Log.v("oneInX", "TRUE " + oneinx);
    		oneintwo++;
    		return true;
    	}
    	else {	
    		//Log.v("oneInX", "FALSE " + oneinx);
    		oneintwo++;
    		return false;
    	}
    }
    
    // nested as in oneInTwo, hence oneInFour
    public boolean oneInFour() {
    	if (oneinfour == 2) {
    		oneinfour = 0;
    	}
    	if (oneinfour == 0) {
    		oneinfour++;
    		return true;
    	}
    	else {	
    		oneinfour++;
    		return false;
    	}
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

    public void draw() {
    	canvas  = holder.lockCanvas();
        
        if(initbackground) {

        	canvas.drawColor(Color.BLACK); 
            
           //canvas.drawBitmap(backg_bitmap, null, screensizerect, null);
        	
        	Paint p1 = new Paint();
            p1.setColor(Color.RED);
            Path path = new Path();
            path.moveTo(100, 300);
            path.lineTo(10, 350);
            path.lineTo(80, 330);
            canvas.drawPath(path, p1);
            
        }

        //this.circle2.drawSequence(canvas);        
        

        for(int i = 0; i < maincircles.length; i++) {
        	maincircles[i].drawSequence(canvas);
        }
        
        this.faderline.drawSequence(canvas);        
        this.twotouch1.drawSequence(canvas);
        this.testxtend.drawSequence(canvas);
        
        this.sonarcircle2.drawSequence(canvas);
    
        holder.unlockCanvasAndPost(canvas);
    }
    

    // ★追加メソッド
    public void startnow(){
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
            	
/*            	if (circle.isAlive()){
            		circle.circleAnim();
            		//circle.circleMod();            		
            	}*/
            	
//            	if (sonarcircle.isAlive()) {
//            		sonarcircle.circleAnim();
//            	}
            	
//            	if (circle.isAlive() || sonarcircle2.isAlive()
//            			|| twotouch1.isAlive() || ) {
//            		draw();
//            	}
            	
            	draw();
            	
            }
        }, 26, 26, TimeUnit.MILLISECONDS); // 28だった

    }
    

	public void nextCirc() {
				
		//int cur = curmaincircle;
		
		if(curmaincircle < maincircles.length) {
			curmaincircle++;
		}
		if (curmaincircle == maincircles.length) {
			curmaincircle = 0;
		}
		//return cur;
	}
    
    protected int getCurmaincircle() {
		return curmaincircle;
	}

	public class Circle2 extends NormalCircle {
    	
    	@Override
    	public void init() {
    		Log.d("Circle2", "init()");
    		float r = this.getRad();
    		
    		this.setPosX((float)((int)(r + 8) + rnd.nextInt(getWidth() - (((int)r + 8) * 2))));
    		this.setPosY((float)((int)(r + 140) + rnd.nextInt(getHeight() - (((int)r + 80) + 140))));
    		this.setRad((float)(130 - rnd.nextInt(50)));
    		
    		this.setARGB(60, rndCol(220), rndCol(220), rndCol(220));
    		    		
    		this.setRadchgspd((float)0.184);
    		//this.setYchgspd((float)-0.674375);
    		this.setYchgspd(-0.1F);
    		this.setAccelangle(0F);
    		
    		this.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
    		this.getPaint().setDither(false);
//    		this.getPaint().setAntiAlias(true);
    		
    		super.init();
    	}
    	
    	@Override
		public void drawSequence(Canvas c) {
    		if (this.isAlive()) {
    	        this.circleAnim();
    	      //  this.circleRadiusMod();
    	        //this.drawCircleFadedEdges(15, 5F, 1, c);
    	        this.drawCircleFadedEdges(5, 15F, 10, c);
    	        
    		}
		}
    
    	@Override
	    public void circleAnim() {	

			int cf = this.getCurrframe();
		
    		if (this.getRelAnim() == false) {
    			
    			if (cf < 600){
		    		this.radIncrement();
		    		this.yIncrement();
		    		
		    		//this.alphaIncrement(2.6F, 50F);
		    		this.alphaDecrement(0.3F, 0F);
	    			//Log.d("circleAnim", "alpha val " + this.getAlpha());
	    			this.frameAdvance();
		    	}
//		    	else if (cf >= 500 && cf < 600) {
//		    		this.frameAdvance();
//				}		
		    	else if (cf == 600) { //アニメーション終了
		    		//this.setAlive(false);
		    	}
    				    			
    		}
    		else {
    			if (cf < 150){
    				this.alphaDecrement(3.5F, 0F); 
    				
		    		this.radIncrement();
		    		this.yIncrement();
    				this.frameAdvance();
    				//Log.d("alpha", "A " + this.getAlpha() + " frame " + this.getCurrframe());
    			}
    			else if (cf == 150) {
    				this.setAlive(false);
    				//Log.d("setAlive", "thisisAlive " + this.isAlive());
    			}
    		}
    		//to switch:
    		
    		// this.speedAccelSamp(1F, -.1F, -60F, 277);
    		this.speedAccelSamp(1F, -.1F, -60F, 225);
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
    		
	    	if (cf < 50){
	    		
	    		this.radIncrement();
    			this.frameAdvance();
    			
	    	}
	    	else if (cf == 50) { 
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
    			if (cf < 70){
    				this.alphaDecrement(2.6F, 0F); 
    				this.frameAdvance();
    				//Log.d("alpha", "A " + this.getAlpha() + " frame " + this.getCurrframe());
    			}
    			else if (cf == 20) {
    				this.setAlive(false);
    				//Log.d("setAlive", "thisisAlive " + this.isAlive());
    			}
    		}
	    }
    	
    	@Override
    	public void drawSequence(Canvas c) {
    		if (this.isAlive()) {
    	        this.circleAnim();
    	        this.circleRadiusMod();
    	        this.drawCircleFadedEdges(3, 20F, 1, c);
    		}
    	}
    }
    
    
    public class NormalLineFader extends NormalLine {
    	
    	@Override
    	public void init() {
    		
    		this.setARGB(45, rndCol(30), rndCol(30), rndCol(30));
    		
    		this.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
    		this.getPaint().setStrokeWidth(2F);
    		//this.getPaint().setDither(true);
    		    		
    		super.init();
    	}
    }
    
}

