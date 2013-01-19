package nz.kapsy.okobotoke;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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
	private float radspd = 45F;
	private float yspd = 0.674375F;
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
		red = r;
		grn = g;
		blu = b;
		
		alphaf = (float)alpha;
		// このメソッドを描く直前に呼ぶ筈だ
		//this.setColor(paint);
		
		this.setRad(80F);
		
	}
	
	// 円形を書くってこと
	public void drawCircleFadedEdges(Canvas c) {
		if (this.isAlive()){
		
            int ciralpha = this.getAlpha();
            int cirred = this.getRed();
            int cirgrn = this.getGrn();
            int cirblu = this.getBlu();
            
            float cirx = this.getPosX();
            float ciry = this.getPosY();
            float cirr = this.getRad();
            
            for (int i = 0; i < 15; i++) {
            	paint.setColor(Color.argb(ciralpha, cirred, cirgrn, cirblu));
                c.drawCircle(cirx, ciry, cirr, this.getPaint());
                
//                Log.d("drawCircle", "Colors " + "a " + ciralpha + " r " + cirred + " g " + cirgrn+ " b " + cirblu);
//                Log.d("drawCircle", "Dimens " + "x " + cirx + " y " + ciry + " r " + cirr);
                
                ciralpha += 1;
                cirr -= 5;
                ciry -= 1;
            }
		}
	}
   	    
	// currframeによって色、形を変える処理
    public void circleAnim() {	
    		
	    	if (currframe < 200){
	    		
	    		//rad = rad + rspd;
	    		//posy = posy - yspd;
    			
    			this.alphaIncrement(1.3F, 7F);
    			
    			//ひつようない
    			// paint.setAlpha(alpha);
    			
    			currframe++;
			
	    	}
	    	else if (currframe >= 500 && currframe < 600) {
	    		currframe++;
			}		
	    	else if (currframe == 600) { //アニメーション終了
	    		alive = false;
	    		
	    		//currframe = 1; // ループなら
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