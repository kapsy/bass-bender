package nz.kapsy.okobotoke;

import java.util.Random;
import java.util.concurrent.*;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

public class MySurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {

    private SurfaceHolder holder;
    
    public Circle circle;
    public SonarCircle sonarcircle;
    public TwoTouchCircle twotouchcircle;
	
	private boolean initbackground;

	Rect screensizerect;
	public Canvas canvas;
	    
	Bitmap backg_bitmap = 
    		BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.bg_test_2);
//	Canvas tmpcanvas;
//	Bitmap tmpScreen;
//	Rect bitmapsaverect;
	
//	public boolean paused;
	
	
    private Random rnd = new Random();
    
    // タッチ処理
    final static int MULTI_TOUCH_MAX = 2;
    private PointF [] touchpoints = new PointF[MULTI_TOUCH_MAX];
    
    


    

//    private Random rndthread = new Random();
//    int rndthreadint;

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
       
      // screensizerect.
        
       
       	circle = new Circle();
        circle.initCircle();
        
        sonarcircle = new SonarCircle();
        sonarcircle.setAlive(false);
        //sonarcircle.initCircle();
       
        twotouchcircle = new TwoTouchCircle();
        twotouchcircle.setAlive(false);
        
        
        initbackground = true;
                
        startnow(); // ★追加

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
/*        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
//            x = event.getX();
//            y = event.getY();
            
//          dropInit();
//          
//this.sonarcircle.setPosx(event.getX());
//this.sonarcircle.setPosy(event.getY());
//this.sonarcircle.initCircle();
        	
        }*/
    	
    	//int action = event.getAction();
    	//outofrangeexception
    	Log.v("Multi", "getPointerCount() " + event.getPointerCount());
    	
    	
    	if (event.getPointerCount() == MULTI_TOUCH_MAX) {
    		
    		 switch (event.getAction() & MotionEvent.ACTION_MASK) {
    		 
//    			case MotionEvent.ACTION_DOWN:
//    				Log.v("Multi", "ACTION_DOWN");
//    				break;
    		 
    	        case MotionEvent.ACTION_POINTER_DOWN:
    	        	Log.v("Multi", "ACTION_POINTER_DOWN");
    	        	// ここで必要ないかも
    	        	this.twotouchcircle.initCircle();
    	        	
    	    		this.twotouchcircle.setAlive(true);
    	    		break;
    	        	
    	    	case MotionEvent.ACTION_MOVE:
    	    		Log.v("Multi", "ACTION_MOVE");
		    		for (int i = 0; i < MULTI_TOUCH_MAX; i++) {
		    			int pointid = event.getPointerId(i);
			    		touchpoints[pointid].x = event.getX(i);
			    		touchpoints[pointid].y = event.getX(i);
		    		}
		    		
		    		
		    		PointF circlecentre = new PointF();
		    		
		    		float radius = 0;
		    		
		    		float xdiff = 0F; 
		    		float ydiff = 0F; 		
		    		
		    		//circlecentre.
		    		
		    		if (touchpoints[0].x < touchpoints[1].x) { 
		    			xdiff = ((touchpoints[1].x - touchpoints[0].x));
		    			circlecentre.x = (touchpoints[0].x + xdiff) / 2;
		    		}
		    		else {
		    			xdiff = ((touchpoints[0].x - touchpoints[1].x));
		    			circlecentre.x = (touchpoints[1].x + xdiff) / 2;
		    		}
		    		
		    		//xdifference.positive
		    		if (touchpoints[0].y < touchpoints[1].y) { 
		    			ydiff = ((touchpoints[1].y - touchpoints[0].y));
		    			circlecentre.y = (touchpoints[0].y + ydiff) / 2;
		    		}
		    		else {
		    			ydiff = ((touchpoints[0].y - touchpoints[1].y));
		    			circlecentre.y = (touchpoints[1].y + ydiff) / 2;
		    		}
		    	
		    		//radius = (float)Math.sqrt(Math.pow((double)xdiff, 2D) + Math.pow((double)ydiff, 2D))/2D;
		    		radius = (float)Math.sqrt((double)((xdiff * xdiff) + (ydiff * ydiff)))/2F;
	    			
		    		
		    		

//		    		// x1、y1 :距離算出座標1つ目
//		    		// x2, y2 :距離算出座標2つ目
//		    		// distance :2点間の距離
//
//		    		double dx = Math.pow(x1 - x2, 2);
//		    		double dy = Math.pow(y1 - y2, 2);
//		    		double distance = Math.sqrt(dx + dy);
//		    		
//		    		double radius = Math.sqrt()
		    		
		    		
		    		
		    		this.twotouchcircle.setPosX(circlecentre.x);
		    		this.twotouchcircle.setPosY(circlecentre.y);
		    		this.twotouchcircle.setRad(radius);
		    		
		    		break;
	    		
    	    	case MotionEvent.ACTION_POINTER_UP:
    	    		// フェードアウト・アニメーション
    	    		Log.v("Multi", "ACTION_POINTER_UP");

    	    		this.twotouchcircle.setAlive(false);
    	    		break;

    		 }
    	}
    	
    	
    	
//		invalidate();
		return true;
        //return super.onTouchEvent(event);
    }
    
/*    public float GetRadius() {
    	
    }*/
    
/*    public void dropInit() {
    	
    x = rnd.nextInt(getWidth());
    y = rnd.nextInt(getHeight());
    	  
        initbackground = false;
        
        
	r = 18 - rnd.nextInt(8);
	biggestmet = false;
	alpha = 1;
	alphaslower = (float)0;
	
	red = rnd.nextInt(1);
	grn = rnd.nextInt(2);
	blu = rnd.nextInt(3);
	//Log.d("COLOR", "Initial Colors: r" + red + " g" + grn + " b" + blu);
	
	redlimit = rnd.nextInt(254 - rnd.nextInt(6));
	grnlimit = rnd.nextInt(254 - rnd.nextInt(6));
	blulimit = rnd.nextInt(245 - rnd.nextInt(6));		
	//Log.d("COLOR", "Color Limits: r" + redlimit + " g" + grnlimit + " b" + blulimit);
	
	//biggestdropsize = rnd.nextInt(100 - rnd.nextInt(40));
	biggestdropsize = 120;
	
	rspeed = (float)0.234;
	yspeed = (float)0.674375;
    	
    }*/

    
/*    public void saveCanvas() {
    	
    	canvas = getHolder().lockCanvas();
    	//別途BitmapとCanvasを用意する


    	//TODO tmpCanvasに対して描画処理を行う

    	//canvas.drawBitmap(tmpScreen, null, mScreenRect, null);
    	//反映
    	getHolder().unlockCanvasAndPost(canvas);
    	
    	
    	
    }
    
    public void restoreCanvas() {
    	
    	canvas  = holder.lockCanvas();
    	

    	//反映
        holder.unlockCanvasAndPost(canvas);
    }
*/

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
            
//            if(paused == false && tmpcanvas !=null) {
//            	// canvas.drawBitmap(tmpScreen, null, bitmapsaverect, null);
//            	canvas.drawBitmap(backg_bitmap, null, screensizerect, null);
//            	Log.d("PauseRes", "canvas.drawBitmap(tmpScreen, null, bitmapsaverect, null)");
//            }
        }
        
//    	if (paused){
//    		//surfaceView からbitmapを取得できないので…やめとけ？
//        	tmpScreen = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//        	//tmpcanvas = new Canvas(tmpScreen);
//        	Log.d("PauseRes", "tmpcanvas = new Canvas(tmpScreen);");
//    	}
        

        Paint pcircle = new Paint();
        pcircle.setStyle(Paint.Style.FILL);
       pcircle.setAntiAlias(false);
        pcircle.setDither(true);
        
         //Log.d("drawCircle", "Colors " + "a " + circle.getAlpha() + " r " + circle.getRed() + " g " + circle.getGrn() + " b " + circle.getBlu());
        
           // Log.d("drawCircle", "Dimens " + "x " + circle.getPosx() + " y " + circle.getPosy() + " r " + circle.getRad());
        
        // cir = circle, values for drawing a shadow effect
        int ciralpha = circle.getAlpha();
        int cirred = circle.getRed();
        int cirgrn = circle.getGrn();
        int cirblu = circle.getBlu();
        
        float cirx = circle.getPosx();
        float ciry = circle.getPosy();
        float cirr = circle.getRad();
        
        for (int i = 0; i < 15; i++) {
        	
        	pcircle.setColor(Color.argb(ciralpha, cirred, cirgrn, cirblu));
            canvas.drawCircle(cirx, ciry, cirr, pcircle); // ★修正
            
            ciralpha += 1;
            cirr -= 5;
            ciry -= 1;
        }
        
        
        int sonalpha = sonarcircle.getAlpha();
        int sonred = sonarcircle.getRed();
        int songrn = sonarcircle.getGrn();
        int sonblu = sonarcircle.getBlu();
        
        float sonx = sonarcircle.getPosx();
        float sony = sonarcircle.getPosy();
        float sonr = sonarcircle.getRad();
        
        float sonstroke = (float)2;
        
        Paint psonarcircle = new Paint();
        psonarcircle.setStyle(Paint.Style.STROKE);
        //psonarcircle.setStrokeWidth((float)1);
        psonarcircle.setAntiAlias(false);
        psonarcircle.setDither(true);
        
        if (sonarcircle.isAlive()) {
        	
        	
/*        	  for (int i = 0; i < 6; i++) {

        	        psonarcircle.setStrokeWidth(sonstroke);
        	psonarcircle.setColor(Color.argb(sonalpha, sonred, songrn, sonblu));
            canvas.drawCircle(sonx, sony, sonr, psonarcircle); // ★修正
            
            sonalpha -= 10;
            sonr -= 15;
           //sonstroke +=3; 
           
        	  }*/
//            Log.d("drawCircle", "Colors " + "a " + sonalpha + " r " + sonred + " g " + songrn+ " b " + sonblu);
//            Log.d("drawCircle", "Dimens " + "x " + sonx + " y " + sony + " r " + sonr);
    
            
           sonalpha = 89;
            //sonr -= 5;
        	sonstroke = (float)20; 
	        psonarcircle.setStrokeWidth(sonstroke);
        	psonarcircle.setColor(Color.argb(sonalpha, sonred, songrn, sonblu));
            canvas.drawCircle(sonx, sony, sonr, psonarcircle); // ★修正
        	
        	
        }
        
        
        int twoalpha = twotouchcircle.getAlpha();
        int twored = twotouchcircle.getRed();
        int twogrn = twotouchcircle.getGrn();
        int twoblu = twotouchcircle.getBlu();
        
        float twox = twotouchcircle.getPosX();
        float twoy = twotouchcircle.getPosY();
        float twor = twotouchcircle.getRad();
        
        
        Paint ptwotouchcircle = new Paint();
        ptwotouchcircle.setStyle(Paint.Style.FILL_AND_STROKE);
        //ptwotouchcircle.setStrokeWidth((float)1);
        ptwotouchcircle.setAntiAlias(false);
        ptwotouchcircle.setDither(false);
        
        if (twotouchcircle.isAlive()) {
          Log.d("drawCircle", "Colors " + "a " + twoalpha + " r " + twored + " g " + twogrn+ " b " + twoblu);
          Log.d("drawCircle", "Dimens " + "x " + twox + " y " + twoy + " r " + twor);
  
          ptwotouchcircle.setColor(Color.argb(100, 255, 255, 255));
            canvas.drawCircle(twox, twoy, twor, ptwotouchcircle); // ★修正
        	
        }
        	
        
        
        
    
        holder.unlockCanvasAndPost(canvas);
    }
    

    // ★追加メソッド
    public void startnow(){
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
            	// 100: 最大 ?: まだ知らない 10: 最初の値
				//            	r = r > 100 ? 10 : r + 1;]
				//            	if (r > 60){}
            	

            	

            	
/*            	if(r > biggestdropsize){
            		biggestmet = true;
            	}

            	if (biggestmet == false) {
            		
            		
            		
            		if (alpha < 7) {            			
            			alphaslower += (float).32;
            			alpha = (int)alphaslower;
            			//Log.d("RUN", "alpha: " + alpha);
            		}
            		

            		y = y + yspeed;
            		r = r +rspeed;
            		rspeed = rspeed + (float).00072;

            	 	if (red <= redlimit){red++;}
            	 	if (grn <= grnlimit){grn++;}
            	 	if (blu <= blulimit){blu++;}
            	 	
            	 	
            	 	
            	 	
            	 	//Log.d("COLOR", "Update Color: r" + red + " g" + grn + " b" + blu);
            	 	draw();
            	}*/
            	
            	
            	
            	if (circle.isAlive()){
            		circle.circleAnim();
            		//circle.circleMod();            		
            	}
            	
            	if (sonarcircle.isAlive()) {
            		sonarcircle.circleAnim();
            	}
            	
            	
            	if (circle.isAlive() || sonarcircle.isAlive() || twotouchcircle.isAlive()) {
            		draw();
            	}
            	
            	
            		
            	

            }
        }, 28, 28, TimeUnit.MILLISECONDS);

    }
    
    //circle animation 
    public class Circle {

		private int alpha;
    	private int red;
    	private int grn;
    	private int blu;
    	
    	private float alphaslower;
	    
    	private float posx;
    	private float posy;
    	private float rad;

    	private float rspd;
    	private float yspd;
    	
    	private int frame;
    	private boolean alive;
    	    	
    	private float sinangle;
    	private float sinanglechangerate;
    	private float modamplitude;
    	    	
    	public void initCircle() {
    		
//    		posx = (float)(80 + rnd.nextInt(getWidth() - 160));
//    		posy = (float)(175 + rnd.nextInt(getHeight() - 210));
//    		
    		rad = 130 - rnd.nextInt(50);
    		
    		posx = (float)((int)(rad + 7) + rnd.nextInt(getWidth() - (((int)rad + 7) * 2)));
    		posy = (float)((int)(rad + 140) + rnd.nextInt(getHeight() - (((int)rad + 75) + 140)));

    		Log.d("POS", "posx " + posx);
    		Log.d("POS", "posy " + posy);
    		
    		alpha = 7;
    		alphaslower = 0;
    		red = 255 - rnd.nextInt(220);
    		grn = 255 - rnd.nextInt(220);
    		blu = 255 - rnd.nextInt(220);
    		
    		rspd = (float)0.184;
    		yspd = (float)0.674375;
    		
    		frame = 1;
    		
    		alive = true;
    		
    		//initbackground = false;
    		
    		sinangle = 0;
    		sinanglechangerate = (float)4.5;
    		modamplitude = (float).8;
    		
    	}
    	
    
	    public void circleAnim() {	
	    		
		    	if (frame < 500){
		    		
		    		rad = rad + rspd;
		    		posy = posy - yspd;
		    		
//        			alphaslower += (float).3;
//        			alpha = (int)alphaslower;
        			
        			frame++;
				
		    	}
		    	else if (frame >= 500 && frame < 600) {
				
					//rad = rad - rspd;
					
					//posy = posy - yspd;
		    		
		    		frame++;
				}		
		    	else if (frame == 500) { //アニメーション終了
		    		alive = false;
		    		//frame = 1;
		    	}
	    }
	    
	    public void circleMod() {
	    	if(sinangle < (float)360 - sinanglechangerate) {
	    		sinangle = sinangle + sinanglechangerate;
	    	}
	    	else {
	    		sinangle = 0;
	    	}

	    	rad = rad + ((float)Math.sin(Math.toRadians((double)sinangle)) * modamplitude);	
	    	//Log.d("sinval", "sine angle: " + sinangle + "result: " 
	    		// + (float)Math.sin(Math.toRadians((double)sinangle)));
	    }

		public boolean isAlive() {
			return alive;
		}
		
		public int getAlpha() {
			return alpha;
		}

		public float getPosx() {
			return posx;
		}

		public void setPosx(float posx) {
			this.posx = posx;
		}

		public float getPosy() {
			return posy;
		}
		
		public void setPosy(float posy) {
			this.posy = posy;
		}

		public float getRad() {
			return rad;
		}

		public int getRed() {
			return red;
		}

		public int getGrn() {
			return grn;
		}

		public int getBlu() {
			return blu;
		}
    
    }
    
    


    //circle animation 
    public class SonarCircle {

		private int alpha;
    	private int red;
    	private int grn;
    	private int blu;
    	
    	private float alphaslower;
	    
    	private float posx;
    	private float posy;
    	private float rad;

    	private float rspd;
    	private float yspd;
    	
    	private int frame;
    	private boolean alive;
    	    	
    	private float sinangle;
    	private float sinanglechangerate;
    	private float modamplitude;
    	    	
    	public void initCircle() {
    		
    		posx = (float)rnd.nextInt(getWidth());
    		posy = (float)rnd.nextInt(getHeight());
    		
    		rad = 20;
    		alpha = 100;
    		alphaslower = 0;
    		red = 255 - rnd.nextInt(30);
    		grn = 0 + rnd.nextInt(15);
    		blu = 0 + rnd.nextInt(15);
    		
    		rspd = (float)45;
    		//yspd = (float)0.674375;
    		
    		frame = 1;
    		
    		alive = true;
    		
    		//initbackground = false;
    		
    		
    		sinangle = 0;
    		sinanglechangerate = (float)4.5;
    		modamplitude = (float).8;
    		
    	}
    	
    
	    public void circleAnim() {	
	    		
		    	if (frame < 500){
		    		
		    		rad = rad + rspd;
		    		//posy = posy - yspd;
		    		
//        			alphaslower += (float).3;
//        			alpha = (int)alphaslower;
        			
        			frame++;
				
		    	}
		    	else if (frame >= 500 && frame < 600) {
				

		    		frame++;
				}		
		    	else if (frame == 500) { //アニメーション終了
		    		alive = false;
		    		//frame = 1;
		    	}
	    }
	    
	    public void circleMod() {
	    	if(sinangle < (float)360 - sinanglechangerate) {
	    		sinangle = sinangle + sinanglechangerate;
	    	}
	    	else {
	    		sinangle = 0;
	    	}

	    	rad = rad + ((float)Math.sin(Math.toRadians((double)sinangle)) * modamplitude);	
	    	//Log.d("sinval", "sine angle: " + sinangle + "result: " + (float)Math.sin(Math.toRadians((double)sinangle)));
	    }

		public boolean isAlive() {
			return alive;
		}
		
		public void setAlive(boolean alive) {
			this.alive = alive;
		}

		public int getAlpha() {
			return alpha;
		}

		public float getPosx() {
			return posx;
		}

		public void setPosx(float posx) {
			this.posx = posx;
		}

		public float getPosy() {
			return posy;
		}
		
		public void setPosy(float posy) {
			this.posy = posy;
		}

		public float getRad() {
			return rad;
		}

		public int getRed() {
			return red;
		}

		public int getGrn() {
			return grn;
		}

		public int getBlu() {
			return blu;
		}
    
    }
    
    //circle animation 
    public class TwoTouchCircle {

		private int alpha;
    	private int red;
    	private int grn;
    	private int blu;
    	
    	private float alphaslower = 0;
	    
    	private float posx;
    	private float posy;
    	private float rad = 0;

    	private float rspd = 45F;
    	private float yspd = 0.674375F;
    	
    	private int frame;
    	private boolean alive;
    	    	
    	private float sinangle = 0F;
    	private float sinanglechangerate = 4.5F;
    	private float modamplitude = 0.8F;
    	    	
    	
    	
    	public TwoTouchCircle () {
    	
    		
    		
    	}
    	
    	
    	
    	public void initCircle() {
    		// setPos メソッドの方がいいかも???
//    		posx = (float)rnd.nextInt(getWidth());
//    		posy = (float)rnd.nextInt(getHeight());
    		
    		rad = 20;
    		alpha = 100;
    		red = 255 - rnd.nextInt(30);
    		grn = 0 + rnd.nextInt(15);
    		blu = 0 + rnd.nextInt(15);
    		
    		//alphaslower = 0;
    		
//    		rspd = (float)45;
//    		yspd = (float)0.674375;
    		
    		frame = 1;
    		
    		alive = true;
    		
    		//initbackground = false;
    		
    		
//    		sinangle = 0;
//    		sinanglechangerate = (float)4.5;
//    		modamplitude = (float).8;
    		
    	}
    	
    
	    public void circleAnim() {	
	    		
		    	if (frame < 500){
		    		
		    		rad = rad + rspd;
		    		//posy = posy - yspd;
		    		
//        			alphaslower += (float).3;
//        			alpha = (int)alphaslower;
        			
        			frame++;
				
		    	}
		    	else if (frame >= 500 && frame < 600) {
				

		    		frame++;
				}		
		    	else if (frame == 500) { //アニメーション終了
		    		alive = false;
		    		//frame = 1;
		    	}
	    }
	    
	    public void circleRadiusMod() {
	    	
	    	if(sinangle < 360F - sinanglechangerate) {
	    		sinangle = sinangle + sinanglechangerate;
	    	}
	    	else {
	    		sinangle = 0;
	    	}

	    	rad = rad + ((float)Math.sin(Math.toRadians((double)sinangle)) * modamplitude);	
	    	//Log.d("sinval", "sine angle: " + sinangle + "result: " + (float)Math.sin(Math.toRadians((double)sinangle)));
	    }

		public boolean isAlive() {
			return alive;
		}
		
		public void setAlive(boolean alive) {
			this.alive = alive;
		}

		public int getAlpha() {
			return alpha;
		}

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

		public float getRad() {
			return rad;
		}

		public void setRad(float rad) {
			this.rad = rad;
		}



		public int getRed() {
			return red;
		}

		public int getGrn() {
			return grn;
		}

		public int getBlu() {
			return blu;
		}
    
    }
    
    
    
  	

    
    

}


