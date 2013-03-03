package nz.kapsy.okobotoke;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

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
				
//		rec_left = 0F;
//		rec_top = totalheight - mysurfv.percToPixY(1F);
//		rec_right = 0F;
//		rec_bottom = totalheight;
		this.initDimensions();
		if (this.framerec.isRecordingnow()) {
			this.setARGB(127, 255, 0, 0);
		}
		if (this.framerec.isPlayingback()) {
			this.setARGB(127, 0, 0, 255);
		}
		super.init();
	}

	public void initDimensions() {
		
		this.setRec_left(0F);
		this.setRec_top(this.getTotalheight() - this.getMysurfv().percToPixY(1F));
		
		this.setRec_right(0F);
		this.setRec_bottom(this.getTotalheight());
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

			// this.mysurfv.setBackGrRed();
			this.mysurfv.recsymbol.init();
		}
		if (this.rec_right == this.incperframe) {

			// this.mysurfv.setBackGrBlack();
		}

		if (this.rec_right < this.totalwidth) {

			this.incBar();

		} else {
			
			
			
			if (this.framerec.isPlayingback()) {
				Log.d("ProgressAnim", "this.framerec.isPlayingback() is true");
				
				//this.mysurfv.releaseAllTouchAnims();
				this.mysurfv.releaseAllTouchPlayAnims();
				this.framerec.startPlayBack();
				this.mysurfv.playsymbol.init();
				
			} else if (this.framerec.isRecordingnow()) {
				Log.d("ProgressAnim", "this.framerec.isRecordingnow() is true");
				
				// タッチ無効する
				this.mysurfv.setTouchenabledafterrec(false);
			//	this.framerec.logAllRecordedFrames();
				this.framerec.startPlayBack();
				//this.mysurfv.releaseAllTouchAnims();
				
				this.mysurfv.releaseAllTouchRecAnims();
				this.mysurfv.setFmrecmode(false);
								
				//大きの方がいい
				this.mysurfv.playsymbolcntr.init();
			}


			this.init();
		}

	}
	
	public void incBar() {
		
		this.rec_right = (this.rec_right + this.incperframe);
	}
	
	
	// fills all rec frames from current frame till the correct list array size
	// (only used when home button is pressed while recording)
	
	public void fillFramesEmpty() {

		if (this.framerec.isRecordingnow()) {
			int totalframes = 0;

			for (float f = 0F; f < this.totalwidth; f = f + this.incperframe) {
				totalframes++;
			}

			this.framerec.setMotionevent(MotionEvent.ACTION_CANCEL);
			this.framerec.setTouchpts(0);

			float c1_x = mysurfv.targtouchfirst[mysurfv.getCurtargtouchfirst()].getPosX();
			float c1_y = mysurfv.targtouchfirst[mysurfv.getCurtargtouchfirst()].getPosY();
			float c2_x = mysurfv.targtouchsecond[mysurfv.getCurtargtouchsecond()].getPosX();
			float c2_y = mysurfv.targtouchsecond[mysurfv.getCurtargtouchsecond()].getPosY();

			int remaining = totalframes - this.framerec.getCurrentframe();

			for (int i = 0; i < remaining; i++) {
				framerec.setFrameMovement(c1_x, c1_y, c2_x, c2_y);
			}
		}

	}

	protected long getTotaltime() {
		return totaltime;
	}

	protected float getRec_left() {
		return rec_left;
	}

	protected float getRec_top() {
		return rec_top;
	}

	protected float getRec_right() {
		return rec_right;
	}

	protected float getRec_bottom() {
		return rec_bottom;
	}

	protected float getTotalheight() {
		return totalheight;
	}

	protected float getTotalwidth() {
		return totalwidth;
	}

	protected int getFrameinterval() {
		return frameinterval;
	}

	protected float getIncperframe() {
		return incperframe;
	}

	protected FrameRecorder getFramerec() {
		return framerec;
	}

	protected MySurfaceView getMysurfv() {
		return mysurfv;
	}

	protected void setTotaltime(long totaltime) {
		this.totaltime = totaltime;
	}

	protected void setRec_left(float rec_left) {
		this.rec_left = rec_left;
	}

	protected void setRec_top(float rec_top) {
		this.rec_top = rec_top;
	}

	protected void setRec_right(float rec_right) {
		this.rec_right = rec_right;
	}

	protected void setRec_bottom(float rec_bottom) {
		this.rec_bottom = rec_bottom;
	}

	protected void setTotalheight(float totalheight) {
		this.totalheight = totalheight;
	}

	protected void setTotalwidth(float totalwidth) {
		this.totalwidth = totalwidth;
	}

	protected void setFrameinterval(int frameinterval) {
		this.frameinterval = frameinterval;
	}

	protected void setIncperframe(float incperframe) {
		this.incperframe = incperframe;
	}

	protected void setFramerec(FrameRecorder framerec) {
		this.framerec = framerec;
	}

	protected void setMysurfv(MySurfaceView mysurfv) {
		this.mysurfv = mysurfv;
	}
	
	

}
