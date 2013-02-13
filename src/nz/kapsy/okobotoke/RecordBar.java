package nz.kapsy.okobotoke;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class RecordBar extends NormalCircle {
	
	

	private long totaltime;
	
	
	private float rec_left;
	private float rec_top;
	private float rec_right;
	private float rec_bottom;
	
	public float totalheight;
	public float totalwidth;
	
	private int frameinterval;//アニメーションの間隔
	private float incperframe;
	
	
	
	private FrameRecorder framerec;
	private MySurfaceView mysurfv;
	
	
	public RecordBar
		(float swidth, float sheight, long totaltime, int frameinterval, 
				FrameRecorder fr, MySurfaceView mysurfv) {
		

		
		
		Paint p = this.getPaint();
		
        p.setStyle(Paint.Style.FILL);
        //p.setStrokeWidth((float)(150 - rnd.nextInt(100)));
        p.setAntiAlias(false);
        p.setDither(false);
        
        this.totaltime = totaltime;
        this.frameinterval = frameinterval;
        
         
        this.totalwidth = swidth;
        this.totalheight = sheight;		
        
        this.calcIncPerFrame();
        
		this.framerec = fr;
		this.mysurfv = mysurfv;
		
		// if restoring after home button pressed
		if (!fr.isPlayingback() && !fr.isRecordingnow()) {
			this.setAlive(false);	
		}
		else {
			this.init();
			fr.startPlayBack();
		}
	}
	
	public void calcIncPerFrame() {
		this.incperframe = this.totalwidth / ((float)this.totaltime / (float)this.frameinterval);
	}
		
	//called when rec or play started
	@Override
	public void init() {
				
		rec_left = 0F;
		rec_top = totalheight - 6F;
		
		rec_right = 0F;
		rec_bottom = totalheight;
			
		
		if (this.framerec.isRecordingnow()) {
			this.setARGB(127, 255, 0, 0);
			
		}
		if (this.framerec.isPlayingback()) {
			this.setARGB(127, 0, 0, 255);
			
		}
		
		
		
		super.init();
		
		
	}

	
	public void startFrameRecorder() {
		
		this.framerec.startRecord();
		
	}
	
	

	
	@Override
	public void drawSequence(Canvas c) {
		if (this.isAlive()) {
			
			
		
			this.progressAnim();
			this.drawProgressBar(c);
		}
	}
	
	public void drawProgressBar(Canvas c) {
		
		
    	this.getPaint().setColor(Color.argb
    			(this.getAlpha(), this.getRed(), this.getGrn(), this.getBlu()));
    	
    	c.drawRect(this.rec_left, this.rec_top, this.rec_right, this.rec_bottom, this.getPaint());
    	
	}
	
	public void progressAnim() {
		// int cf = this.getCurrframe();

		if (this.rec_right == 0 && this.framerec.isRecordingnow()) {

			// this.mysurfv.getBground().setRed();
			this.mysurfv.setBackGrRed();

		}
		if (this.rec_right == this.incperframe) {
			// this.mysurfv.getBground().setBlack();
			this.mysurfv.setBackGrBlack();
		}

		if (this.rec_right < this.totalwidth) {

			this.incBar();

		} else {

			if (this.framerec.isRecordingnow()) {

				// タッチ無効する
				this.mysurfv.setTouchenabled(false);
				this.mysurfv.releaseAllTouchAnims();
				// this.framerec.forceLastFrameOff();
				this.framerec.startPlayBack();
				// this.mysurfv.getBground().setBlue();
				this.mysurfv.setBackGrBlue();
			}
			// 再生ループ
			if (this.framerec.isPlayingback()) {
				this.mysurfv.releaseAllTouchAnims();
				this.framerec.startPlayBack();
			}

			this.init();
		}

	}
	
	public void incBar() {
		
		this.rec_right = (this.rec_right + this.incperframe);
	}

	
	

}
