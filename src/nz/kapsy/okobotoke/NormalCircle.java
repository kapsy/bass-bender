package nz.kapsy.okobotoke;

import java.util.Random;

import nz.kapsy.okobotoke.MySurfaceView.TargetTouch;

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
	private float rad = 0F;
	
	// アニメーションのためパラメーター
	private float radchgspd = 0F;
	// linear accel/decel
	private float linearyaccelfactor = 1F;
	private float linearxaccelfactor = 1F;
	
	// linear speed holders
	private float ychgspd = 0.674375F;
	private float xchgspd = 0F;

	// target decel vals
	private float maxaccelspeedx = 45F;
	private float maxaccelspeedy = 45F;
	private float acceltargetx;
	private float acceltargety;
	


	
	private int currframe;
	private boolean playrelanim = false;
	    	
	// mod フィルド
	private float accelangle = 0F;
	
	private float radfadeangle = 0F;
	private float baserad = 0F; //original radius before any modulation modifiers
	private float radmodsinangle = 0F;
	
	private TargetTouch targetpoint1;
	
	private NormalCircle targetcircle1;
	
	public NormalCircle() {

		this.setAlive(false);

		paint.setStyle(Paint.Style.FILL);
		// paint.setStrokeWidth((float)(150 - rnd.nextInt(100)));
		paint.setAntiAlias(false);
		paint.setDither(false);

	}

	// 初期化
	// 円形を描く前、このメソッドを呼びなくて駄目
	public void init(int a, int r, int g, int b) {
		// float x, float y, float r

		// 必要処理
		alive = true;
		currframe = 1;
		playrelanim = false;

		alpha = a;
		alphaf = (float) alpha;

		red = r;
		grn = g;
		blu = b;

	}

	public void init() {

		// 必要処理
		alive = true;
		currframe = 1;
		playrelanim = false;

		alphaf = (float) alpha;

	}

	public void drawSequence(Canvas c) {
		if (this.isAlive()) {
			//this.circleAnim();
			//this.circleRadiusMod();
			this.drawCircleFadedEdges(15, 5F, 1, c);
		}
	}
	
	// 円形を書くってこと
	public void drawCircleFadedEdges(int layers, float rchg, int alphachng, Canvas c) {
		
        int ciralpha = this.getAlpha();
        int cirred = this.getRed();
        int cirgrn = this.getGrn();
        int cirblu = this.getBlu();
        
        float cirx = this.getPosX();
        float ciry = this.getPosY();
        float cirr = this.getRad();
        //float rchgreal = (cirr / 100) * rchg;
        
        for (int i = 0; i < layers; i++) {
        	paint.setColor(Color.argb(ciralpha, cirred, cirgrn, cirblu));
            c.drawCircle(cirx, ciry, cirr, this.getPaint());
            
//                Log.d("drawCircle", "Colors " + "a " + ciralpha + " r " + cirred + " g " + cirgrn+ " b " + cirblu);
//                Log.d("drawCircle", "Dimens " + "x " + cirx + " y " + ciry + " r " + cirr);
            
            if (ciralpha > 0) {
            	ciralpha += alphachng;
            }
            
            //ciralpha += 1;
            cirr -= rchg;
            ciry -= 2;
        }
	}
	
	public void drawCircleOnce(Canvas c) {
		
        	paint.setColor(Color.argb(alpha, red, grn, blu));
            c.drawCircle(posx, posy, rad, this.getPaint());
           
	}
	
	public void drawCircleOnce(int col, Canvas c) {

		paint.setColor(col);
		c.drawCircle(posx, posy, rad, this.getPaint());

	}
	
	public void drawCircleOnceNoColor(Canvas c) {
		    	
        c.drawCircle(posx, posy, rad, this.getPaint());
       
	}

	// currframeによって色、形を変える処理
	public void circleAnim() {

		int cf = this.getCurrframe();

		if (cf < 200) {

			this.alphaIncrement(1.3F, 7F);

			this.frameAdvance();

		} else if (cf >= 500 && cf < 600) {
			this.frameAdvance();
		} else if (cf == 600) { // アニメーション終了
			this.setAlive(false);

			// ループなら this.setCurrframe(1);
		}
	}
    

    
	// mod amplitude %
	public void cirRadModSamp(float anglechgrate, float modamplitude) {

		if (this.radmodsinangle < 360F - anglechgrate) {
			this.radmodsinangle = this.radmodsinangle + anglechgrate;
		} else {
			this.radmodsinangle = 0;
		}
		// returns -1 to 1
		float sinval = SampledSines.getPosNegSineVal(this.radmodsinangle);
		this.rad = this.baserad + (sinval * modamplitude);
		// Log.d("cirRadModSamp", "radmodsinangle: " + radmodsinangle
		// + " mod values: " + (sinval * modamplitude));
	}
    
    public void speedAccelSamp(float changeangle, float initialspeed, float targetspeed, float startangle) {
    	
    	float targetdiff = targetspeed - initialspeed;
    	
    	if ((this.accelangle + startangle) < 360F - changeangle) {
    
    		float sval = SampledSines.getPosSineVal(this.accelangle + startangle);
    		this.setYchgspd(initialspeed + (targetdiff * sval));
    		
    		//Log.d("speedAccelSamp", "this.getYchgspd(): " + this.getYchgspd());
        	this.accelangle = this.accelangle + changeangle;
    	}
    }
    
    
    
	public void linearXAccel() {

		this.setXchgspd(this.getXchgspd() * this.linearxaccelfactor);
	}

	public void linearYAccel() {

		this.setYchgspd(this.getYchgspd() * this.linearyaccelfactor);
	}

	// deccelerates towards whatever target is set to
	public void xCalcSpeed(float width) {

		this.setXchgspd(((this.acceltargetx - this.getPosX()) / width)
				* this.maxaccelspeedx);
	}

	public void yCalcSpeed(float height) {

		this.setYchgspd(((this.acceltargety - this.getPosY()) / height)
				* this.maxaccelspeedy);
	}

	protected TargetTouch getTargetpoint1() {
		return targetpoint1;
	}
	protected void setTargetpoint1(TargetTouch targetpoint1) {
		this.targetpoint1 = targetpoint1;
	}

	protected void getCoordsFromTarget() {
		if (this.targetpoint1 != null) {
			
			this.setTargetXy(this.targetpoint1.getPosX(),  this.targetpoint1.getPosY());
		}
	}
	
	protected NormalCircle getTargetcircle1() {
		return targetcircle1;
	}
	protected void setTargetcircle1(NormalCircle targetcircle1) {
		this.targetcircle1 = targetcircle1;
	}

	protected void getCoordsFromTargetCircle() {
		if (this.targetcircle1 != null) {
			
			this.setTargetXy(this.targetcircle1.getPosX(),  this.targetcircle1.getPosY());
		}
	}
	
	
	
	
	
	// target for decel
	protected void setTargetXy(float targetx, float targety) {
		this.acceltargetx = targetx;
		this.acceltargety = targety;
	}
    
    protected float getMaxaccelspeedx() {
		return maxaccelspeedx;
	}

	protected float getMaxaccelspeedy() {
		return maxaccelspeedy;
	}

	protected void setMaxaccelspeedx(float maxaccelspeedx) {
		this.maxaccelspeedx = maxaccelspeedx;
	}

	protected void setMaxaccelspeedy(float maxaccelspeedy) {
		this.maxaccelspeedy = maxaccelspeedy;
	}

	protected float getAcceltargetx() {
		return acceltargetx;
	}

	protected float getAcceltargety() {
		return acceltargety;
	}

	protected void setAcceltargetx(float acceltargetx) {
		this.acceltargetx = acceltargetx;
	}

	protected void setAcceltargety(float acceltargety) {
		this.acceltargety = acceltargety;
	}

	//curveの方はどうだ？
    // 180 -> 270
    public void radFade(float angchgrate, float startang, float finang, float raddiff) {

    	//this.baserad = this.getRad();
    	
    	if ((this.radfadeangle + startang) < finang) {
    		
    		float sval = SampledSines.getPosSineVal(this.radfadeangle + startang);
    		this.setRad((raddiff * sval) + this.baserad);

    		//Log.d("radFade", "sval: " + sval + " this.getRad(): " + this.getRad());
    		    		
    		this.radfadeangle = this.radfadeangle + angchgrate;
    		
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
		else {
			this.alpha = (int)max;
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

	// sets baserad - for retaining original radius if applying mod
	public void initRad(float rad) {
		this.rad = rad;
		this.baserad = this.rad;
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
	
	protected float getLinearxaccelfactor() {
		return linearxaccelfactor;
	}

	protected void setLinearxaccelfactor(float linearxaccelfactor) {
		this.linearxaccelfactor = linearxaccelfactor;
	}

	protected float getLinearyaccelfactor() {
		return linearyaccelfactor;
	}

	protected void setLinearyaccelfactor(float linearyaccelfactor) {
		this.linearyaccelfactor = linearyaccelfactor;
	}

	protected void xIncrement() {
		this.posx = this.posx + this.xchgspd;
	}

	protected float getXchgspd() {
		return xchgspd;
	}

	protected void setXchgspd(float xchgspd) {
		this.xchgspd = xchgspd;
	}

	protected float getAccelangle() {
		return accelangle;
	}

	protected void setAccelangle(float accelangle) {
		this.accelangle = accelangle;
	}

	protected float getRadfadeangle() {
		return radfadeangle;
	}


	protected void setRadfadeangle(float radfadeangle) {
		this.radfadeangle = radfadeangle;
	}


	protected void setBaserad(float baserad) {
		this.baserad = baserad;
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
	
	public void setRGB (int r, int g, int b) {
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

	protected float getAlphaf() {
		return alphaf;
	}

	protected void setAlphaf(float alphaf) {
		this.alphaf = alphaf;
	}

	protected boolean isPlayrelanim() {
		return playrelanim;
	}

	protected void setPlayrelanim(boolean playrelanim) {
		this.playrelanim = playrelanim;
	}

}