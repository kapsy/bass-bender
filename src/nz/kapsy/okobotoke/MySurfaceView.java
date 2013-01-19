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
    
    
    public NormalCircle twotouch1;
    public NormalCircle twotouch2;
    
    public NormalCircleMultiTouch testxtend;
	
	private boolean initbackground;

	Rect screensizerect;
	public Canvas canvas;
	    
	Bitmap backg_bitmap = 
    		BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.bg_test_2);

//	Rect bitmapsaverect;
	
	
    private Random rnd = new Random();
    
    // タッチ処理
    final static int MULTI_TOUCH_MAX = 2;
    private PointF [] touchpoints = new PointF[MULTI_TOUCH_MAX];
    
    int oneinx = 0;
    
    

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
       
//        twotouchcircle = new TwoTouchCircle();
//        twotouchcircle.setAlive(false);
        
        twotouch1 = new NormalCircle();
        twotouch2 = new NormalCircle();
        
        testxtend = new NormalCircleMultiTouch();
        
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

        	
        }*/
    	
    	//int action = event.getAction();
    	//outofrangeexception
    	//Log.v("Multi", "getPointerCount() " + event.getPointerCount());
    	
    	
    	if (event.getPointerCount() == MULTI_TOUCH_MAX) {
    		
    		 switch (event.getAction() & MotionEvent.ACTION_MASK) {
    		 
//    			case MotionEvent.ACTION_DOWN:
//    				Log.v("Multi", "ACTION_DOWN");
//    				break;
    		 
    	        case MotionEvent.ACTION_POINTER_DOWN:
    	        	//Log.v("Multi", "ACTION_POINTER_DOWN");
    	        	// ここで必要ないかも
    	        	
    	        	// this.twotouchcircle.initCircle();
    	        	
    	        	this.twotouch1.init(7, rndCol(130), rndCol(130), rndCol(130));
    	        	//this.twotouch2.init();
    	        	this.testxtend.init(0, rndCol(130), rndCol(130), rndCol(130));
    	       
    	        	
    	    		break;
    	        	
    	    	case MotionEvent.ACTION_MOVE:
    	    		//Log.v("Multi", "ACTION_MOVE");
    	    		
//    	    		if (oneInX()) {
//		    		for (int i = 0; i < MULTI_TOUCH_MAX; i++) {
//		    			int pointid = event.getPointerId(i);
//			    		touchpoints[pointid].x = event.getX(i);
//			    		touchpoints[pointid].y = event.getX(i);
//		    		}
//		    		
//		    		
//		    		PointF circlecentre = new PointF();
//		    		
//		    		float radius = 0;
//		    		
//		    		float xdiff = 0F; 
//		    		float ydiff = 0F; 		
//    		
//		    		if (touchpoints[0].x < touchpoints[1].x) { 
//		    			xdiff = ((touchpoints[1].x - touchpoints[0].x));
//		    			circlecentre.x = (touchpoints[0].x + xdiff) / 2;
//		    		}
//		    		else {
//		    			xdiff = ((touchpoints[0].x - touchpoints[1].x));
//		    			circlecentre.x = (touchpoints[1].x + xdiff) / 2;
//		    		}
//		    		
//		    		//xdifference.positive
//		    		if (touchpoints[0].y < touchpoints[1].y) { 
//		    			ydiff = ((touchpoints[1].y - touchpoints[0].y));
//		    			circlecentre.y = (touchpoints[0].y + ydiff) / 2;
//		    		}
//		    		else {
//		    			ydiff = ((touchpoints[0].y - touchpoints[1].y));
//		    			circlecentre.y = (touchpoints[1].y + ydiff) / 2;
//		    		}
//		    			    		
//		    		radius = (float)Math.sqrt((double)((xdiff * xdiff) + (ydiff * ydiff)))/2F;
//	    					    		
//		    		this.twotouchcircle.setPosX(circlecentre.x);
//		    		this.twotouchcircle.setPosY(circlecentre.y);
//		    		this.twotouchcircle.setRad(radius);
//		    		
//		    		
//    	    		}
		    		
    	    		
/*    	    		if (oneInX()) {
		    		this.twotouchcircle.setLinePoints(event.getX(0), event.getY(0), 
		    				event.getX(1), event.getY(1));
		    		Log.v("Multi", "ACTION_MOVE" + " x1 " + event.getX(0) + " y1 " + event.getY(0) + 
		    				" x2 " + event.getX(1) + " y2 " + event.getY(1));
    	    		}*/
    	    		if (oneInX()) {
    	    		this.twotouch1.setPosX(event.getX(0));
    	    		this.twotouch1.setPosY(event.getY(0));
    	    		
      	    		this.testxtend.setPosX(event.getX(1));
    	    		this.testxtend.setPosY(event.getY(1));
    	    		}
//		    		Log.v("Multi", "CO-ORDS" + " x1 " + event.getX(0) + " y1 " + event.getY(0) + 
//		    				" x2 " + event.getX(1) + " y2 " + event.getY(1));
    	    		
    	    		
    	    		
		    		break;
	    		
    	    	case MotionEvent.ACTION_POINTER_UP:
    	    		// フェードアウト・アニメーション
    	    		//Log.v("Multi", "ACTION_POINTER_UP");

    	    		this.twotouch1.setAlive(false);
    	    		this.testxtend.relAnimOn();
    	    		break;

    		 }
    	}
    	
    	
    	
//		invalidate();
		return true;
        //return super.onTouchEvent(event);
    }
    
 

    public boolean oneInX() {
    	
    	if (oneinx == 2) {
    		oneinx = 0;
    	}
    	
    	if (oneinx == 0) {
    		//Log.v("oneInX", "TRUE " + oneinx);
    		oneinx++;
    		return true;
    	}
    	else {	
    		//Log.v("oneInX", "FALSE " + oneinx);
    		oneinx++;
    		return false;
    	}
    }
    
    public int rndCol(int scope) {
    	return (255 - rnd.nextInt(scope));
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
        
        
        
        
//        if (twotouchcircle.isAlive()) {
//    twotouchcircle.drawLine(canvas);
//
//          }
        
        this.twotouch1.drawCircleFadedEdges(canvas);
        this.testxtend.drawCircleFadedEdges(canvas);
        	
    
        holder.unlockCanvasAndPost(canvas);
    }
    

    // ★追加メソッド
    public void startnow(){
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
            	
            	if (circle.isAlive()){
            		circle.circleAnim();
            		//circle.circleMod();            		
            	}
            	
            	if (sonarcircle.isAlive()) {
            		sonarcircle.circleAnim();
            	}
            	
            	if (circle.isAlive() || sonarcircle.isAlive()) {
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
    		
    		posx = (float)((int)(rad + 8) + rnd.nextInt(getWidth() - (((int)rad + 8) * 2)));
    		posy = (float)((int)(rad + 140) + rnd.nextInt(getHeight() - (((int)rad + 80) + 140)));

    		Log.d("POS", "posx " + posx);
    		Log.d("POS", "posy " + posy);
    		
    		alpha = 19;
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
    	
    	private Paint paint;
    	    	
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
    
    //TODO 実装メッソドに変更
    
/*    //circle animation 
    public class TwoTouchCircle {

		private int alpha;
    	private int red;
    	private int grn;
    	private int blu;
    	
    	private float alphaslower = 1;
	    
    	private float posx;
    	private float posy;
    	private float rad = 0;
    	
    	
    	private Paint paint = new Paint();
    	
    	private float[] linepoints = new float[4];
    	
    	

    	private float rspd = 45F;
    	private float yspd = 0.674375F;
    	
    	private int frame;
    	private boolean alive;
    	    	
    	private float sinangle = 0F;
    	private float sinanglechangerate = 4.5F;
    	private float modamplitude = 0.8F;
    	    	
    	
    	
    	public TwoTouchCircle () {
    	
    		
    		
    	}
    	
    	public void setLinePoints(float x1, float y1, float x2, float y2) {
    		linepoints[0] = x1;
    		linepoints[1] = y1;
    		linepoints[2] = x2;
    		linepoints[3] = y2;
  
    	}
    	
    	public void drawLine(Canvas c) {
    		
    		this.circleAnim();
    		c.drawLine(linepoints[0], linepoints[1], linepoints[2], linepoints[3], this.getPaint());
    		
    		// Log.d("drawLine", " lp[0] " + linepoints[0] + " lp[1] " + linepoints[1] + 
    		//		" lp[2] " + linepoints[2] + " lp[3] " + linepoints[3]); 
    	}
    	
    	public void initCircle() {
    		// setPos メソッドの方がいいかも???
//    		posx = (float)rnd.nextInt(getWidth());
//    		posy = (float)rnd.nextInt(getHeight());
    		
    		//rad = 20;
    		//alpha = 100;
    		alphaslower = 0F;
    		red = 255 - rnd.nextInt(130);
    		grn = 255 - rnd.nextInt(130);
    		blu = 255 - rnd.nextInt(130);
    		
    		//alphaslower = 0;
    		
//    		rspd = (float)45;
//    		yspd = (float)0.674375;
    		
    		frame = 1;
    		
    		alive = true;
    		
    		//initbackground = false;
    		
    		
//    		sinangle = 0;
//    		sinanglechangerate = (float)4.5;
//    		modamplitude = (float).8;
    		
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth((float)(150 - rnd.nextInt(100)));
            paint.setAntiAlias(false);
            paint.setDither(false);
            paint.setColor(Color.argb(150, red, grn, blu));
    	}
    	

    	
    
	    public void circleAnim() {	
	    		
		    	if (frame < 200){
		    		
		    		//rad = rad + rspd;
		    		//posy = posy - yspd;
		    		
        			alphaslower += (float)1.3;
        			alpha = (int)alphaslower;
        			
        			paint.setAlpha(alpha);
        			
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

		public Paint getPaint() {
			return paint;
		}
    
    }*/
        
    public class NormalCircleMultiTouch extends NormalCircle {
    	    	
    	@Override
    	public void drawCircleFadedEdges(Canvas c) {
    		//this.setColor(this.getPaint());
    		
    		if (this.isAlive()){
    			    			
	            this.circleAnim();
	            this.circleRadiusMod();
        		
	            int ciralpha = this.getAlpha();
	            int cirred = this.getRed();
	            int cirgrn = this.getGrn();
	            int cirblu = this.getBlu();
	            
	            float cirx = this.getPosX();
	            float ciry = this.getPosY();
	            float cirr = this.getRad();
	            
	            
	            for (int i = 0; i < 3; i++) {
	            	this.getPaint().setColor(Color.argb(ciralpha, cirred, cirgrn, cirblu));
	                c.drawCircle(cirx, ciry, cirr, this.getPaint());
	                
//	                Log.d("drawCircle", "Colors " + "a " + ciralpha + " r " + cirred + " g " + cirgrn+ " b " + cirblu);
//	                Log.d("drawCircle", "Dimens " + "x " + cirx + " y " + ciry + " r " + cirr);
	                if (ciralpha < 0) {
	                	ciralpha += 1;
	                }
	                cirr -= 20;
	                ciry -= 1;
	            }
    		
	            
//                Log.d("SimpleCircle", "Colors " + "a " + this.getAlpha() + " r " + this.getRed() 
//                		+ " g " + this.getGrn()+ " b " + this.getBlu());
//                Log.d("SimpleCircle", "Dimens " + "x " + this.getPosX() + " y " + this.getPosY() 
//                		+ " r " + this.getRad());
    		}    		
    	}
    	
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
			    		//currframe = 1; // ループなら
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
    	
    }
    
}


