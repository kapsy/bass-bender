package nz.kapsy.okobotoke;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

// 新しい刷新した、全般的な円形を書くクラス。
// 実装ができるくらすです。
public class NormalCircle {
	
	private boolean alive;

	// 演算するときfloatを使ってintに変更するためのフィルド
	private float alphaf = 1F;
	private int alpha;
	private int red;
	private int grn;
	private int blu;
	
	private Paint paint = new Paint();
    
	private float posx;
	private float posy;
	private float rad = 0;
	
	// アニメーションのためパラメーター
	private float radchgspd = 45F;
	private float ychgspd = 0.674375F;
	private float accelangle = 0F;
	private int currframe;
	
	private boolean playrelanim = false;
	    	
	private float sinangle = 0F;
	private float sinanglechangerate = 4.5F;
	private float modamplitude = 0.8F;
	
	public NormalCircle () {
	    
		this.setAlive(false);
		
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        //paint.setStrokeWidth((float)(150 - rnd.nextInt(100)));
        paint.setAntiAlias(false);
        paint.setDither(false);
        
       
		
	}

	
	// 初期化
	// 円形を描く前、このメソッドを呼びなくて駄目
	public void init(int a, int r, int g, int b) {
		//float x, float y, float r
		
		// 必要処理
		alive = true;
		currframe = 1;
		playrelanim = false;
		
//		this.setPosX(x);
//		this.setPosY(y);
//		this.setRad(r);
		
		
//		alpha = 7;
//		red = 255 - rnd.nextInt(130);
//		grn = 255 - rnd.nextInt(130);
//		blu = 255 - rnd.nextInt(130);
		
		alpha = a;
		alphaf = (float)alpha;
		
		red = r;
		grn = g;
		blu = b;
		
		// このメソッドを描く直前に呼ぶ筈だ
		//this.setColor(paint);
		
		//this.setRad(80F);
		
	}
	
	public void init() {

		// 必要処理
		alive = true;
		currframe = 1;
		playrelanim = false;
		
		alphaf = (float)alpha;
		
	}
	
	public void drawSequence(Canvas c) {
		if (this.isAlive()) {
			//this.circleAnim();
			//this.circleRadiusMod();
			this.drawCircleFadedEdges(15, 5F, 1, c);
		}
	}
	
	// 円形を書くってこと
	public void drawCircleFadedEdges(int layers, float rchng, int alphachng, Canvas c) {
		
        int ciralpha = this.getAlpha();
        int cirred = this.getRed();
        int cirgrn = this.getGrn();
        int cirblu = this.getBlu();
        
        float cirx = this.getPosX();
        float ciry = this.getPosY();
        float cirr = this.getRad();
        
        for (int i = 0; i < layers; i++) {
        	paint.setColor(Color.argb(ciralpha, cirred, cirgrn, cirblu));
            c.drawCircle(cirx, ciry, cirr, this.getPaint());
            
//                Log.d("drawCircle", "Colors " + "a " + ciralpha + " r " + cirred + " g " + cirgrn+ " b " + cirblu);
//                Log.d("drawCircle", "Dimens " + "x " + cirx + " y " + ciry + " r " + cirr);
            
            if (ciralpha > 0) {
            	ciralpha += alphachng;
            }
            
            //ciralpha += 1;
            cirr -= rchng;
            ciry -= 1;
        }
	}
	
	public void drawCircleOnce(Canvas c) {
		
        	paint.setColor(Color.argb(alpha, red, grn, blu));
            c.drawCircle(posx, posy, rad, this.getPaint());
            
//            Log.d("drawCircleOnce", "Colors " + "a " + alpha + " r " + red + " g " + grn+ " b " + blu);
//            Log.d("drawCircleOnce", "Dimens " + "x " + posx + " y " + posy + " r " + rad);

	}
   	    
	// currframeによって色、形を変える処理
    public void circleAnim() {	
    	
    	int cf = this.getCurrframe();
		
    	if (cf < 200){
    		
    		//rad = rad + rspd;
    		//posy = posy - yspd;
			
			this.alphaIncrement(1.3F, 7F);
			
			this.frameAdvance();
		
    	}
    	else if (cf >= 500 && cf < 600) {
    		this.frameAdvance();
		}		
    	else if (cf == 600) { //アニメーション終了
    		this.setAlive(false);
    		
    		// ループなら this.setCurrframe(1);
    	}
    }
    
    public void circleRadiusMod() {
    	//if (this.isAlive()) {
    		if(sinangle < 360F - sinanglechangerate) {
    			sinangle = sinangle + sinanglechangerate;
	    	}
	    	else {
	    		sinangle = 0;
	    	}
	    	rad = rad + ((float)Math.sin(Math.toRadians((double)sinangle)) * modamplitude);	
	    	//Log.d("sinval", "sine angle: " + sinangle + "result: " + (float)Math.sin(Math.toRadians((double)sinangle)));
    	//}
    }
    
/*    public void speedAccel(float changeangle, float initialspeed, float targetspeed, float startangle) {
    	    	
    	float targetdiff = targetspeed - initialspeed;
    	this.accelangle = this.accelangle + changeangle;
    	
    	if((accelangle + startangle) < 360F - changeangle) {
    		this.setYchgspd(initialspeed + (targetdiff * (((float)Math.sin(Math.toRadians((double)accelangle + startangle))) + 1F)));
//    		Log.d("sineq", "accelangel into sin: " + ((double)accelangle + startangle) 
//    				+ " sin eq: " + ((((float)Math.sin(Math.toRadians((double)accelangle + startangle))) + 1F) / 2));
//    		Log.d("speedAccelif", "initialspeed " + initialspeed + " targetdiff " + targetdiff 
//    				+ " accelangle " + accelangle + " ySpeed " + this.getYchgspd());
    	}
	}*/
    
    public void speedAccelSamp(float changeangle, float initialspeed, float targetspeed, float startangle) {
    	
    	float targetdiff = targetspeed - initialspeed;
    	
    	if ((this.accelangle + startangle) < 360F - changeangle) {
    
    		float val = SampledSines.getPosSineVal(this.accelangle + startangle);
    		this.setYchgspd(initialspeed + (targetdiff * val));
    		
    		//Log.d("speedAccelSamp", "this.getYchgspd(): " + this.getYchgspd());
        	this.accelangle = this.accelangle + changeangle;
    	}
    }
    
	public void relAnimOn() {
		
		this.playrelanim = true;
		this.setCurrframe(0);
	}
    
	protected boolean getRelAnim() {
		return playrelanim;
	}
	
	// アニメーションのためのメソッド
	
	protected void alphaIncrement(float inc, float max) {
		
		if (this.alpha < (max - inc)) {
			alphaf += inc;
			this.alpha = (int)alphaf;
		}
	}

	protected void alphaIncrement(float inc) {
			alphaf += inc;
			this.alpha = (int)alphaf;
	}
	
	protected void alphaDecrement (float dec, float min) {
		
		// float -> int for slower fade outs 
		if (this.alpha > (min + dec)) {
			alphaf -= dec;
			this.alpha = (int)alphaf;
		}
		else {
			this.alpha = (int)min;
		}
	}
	
    // getterとsetter
	public boolean isAlive() {
		return alive;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
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
	
	protected void radIncrement() {
		this.rad = this.rad + this.radchgspd;
	}

	protected float getRadchgspd() {
		return radchgspd;
	}

	protected void setRadchgspd(float radchgspd) {
		this.radchgspd = radchgspd;
	}
	
	protected void yIncrement() {
		this.posy = this.posy + this.ychgspd;
	}

	protected float getYchgspd() {
		return ychgspd;
	}

	protected void setYchgspd(float ychgspd) {
		this.ychgspd = ychgspd;
	}

	protected float getAccelangle() {
		return accelangle;
	}

	protected void setAccelangle(float accelangle) {
		this.accelangle = accelangle;
	}

	public void setColor(Paint p) {
        p.setColor(Color.argb(alpha, red, grn, blu));
	}
			
	public Paint getPaint() {
		return paint;
	}
	
	public int getAlpha() {
		return alpha;
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public void setARGB (int a, int r, int g, int b) {
		this.alpha = a;
		this.red = r;
		this.grn = g;
		this.blu = b;
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

	protected int getCurrframe() {
		return currframe;
	}

	protected void setCurrframe(int currframe) {
		this.currframe = currframe;
	}
	
	protected void frameAdvance() {
		this.currframe++;
	}

}