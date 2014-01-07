package nz.kapsy.bassbender;

import java.util.ArrayList;

import android.util.Log;
import android.view.MotionEvent;

public class FrameRecorder {
	
	private boolean recordingnow;
	private boolean playingback;
		
	private int currentframe;
	// records last motion event obtained from touch
	private int motionevent = MotionEvent.ACTION_CANCEL;
	private int touchpts = 0;
	
	private ArrayList<FrameRecUnit> recording = 
			new ArrayList<FrameRecUnit>();
			
	public void startRecord() {
		
		if (!this.recording.isEmpty())
		{
			this.recording.clear();
		}
		this.setPlayingback(false);
		this.setRecordingnow(true);
		this.setTouchpts(0);
		this.setMotionevent(MotionEvent.ACTION_CANCEL);
	}
	
	public void startPlayBack() {

		// 強制的にタッチ処理を終了ってこと
		this.setRecordingnow(false);
		
		if (this.recording.size() > 0) {
			this.setPlayingback(true);
		}
		this.setCurrentframe(0);
	}
			
	// adds important events at the time they occur
	public void setFrameEssential(
			float x0, float y0, float x1, float y1,
			int motionevent, int touchpts, int index) {
		
		if (this.isRecordingnow()) {
			this.recording.add(new FrameRecUnit(
					x0, y0, x1, y1, motionevent, touchpts, index));
		}
		// resets the current motionevent so that movements aren't recorded
		this.setMotionevent(MotionEvent.ACTION_CANCEL);
	}
	
	// called while drawing 
	// records only movement/non movement frames
	public void setFrameMovement(
			float x0, float y0, 
			float x1, float y1) {
		
		if (this.isRecordingnow()) {
			// index = 99 because is irrelevant for movement
			// index = 99、理由は動くを再現するため無関係である
			this.recording.add(new FrameRecUnit(
					x0, y0, x1, y1, this.getMotionevent(), this.getTouchpts(), 99));
		}
	}
	
	public void logAllRecordedFrames() {
		
		if (this.recording != null) {
			for (int i = 0; i < this.recording.size(); i++) {
				FrameRecUnit fl = this.recording.get(i);
				Log.d("recording", "RECORDED FRAME"
					+ "\n" + "getCirtfirstx " + fl.getCirtfirstx() 
					+ "\n" + "getCirtfirsty " +  fl.getCirtfirsty() 
					+ "\n" + "getCirtsecondx " + fl.getCirtsecondx() 
					+ "\n" + "getCirtsecondy " + fl.getCirtsecondy() 
					+ "\n" + "getTouchpts " + fl.getTouchpts() 
					+ "\n" + "getMotionevent " + fl.getMotionevent()
					+ "\n" + "getIndex " + fl.getIndex()
					+ "\n" + "recording.size() " + recording.size());
			}
		}
	}

	public FrameRecUnit getPlaybackFrame() {
			
		FrameRecUnit f = recording.get(this.getCurrentframe());

/*			Log.d("recording", 
					"f.getMotionevent(): " + f.getMotionevent()
					+ "\n" + "f.getTouchpts(): " + f.getTouchpts()
					+ "\n" + "this.getCurrentframe(): " + this.getCurrentframe()
					+ "\n" + " ");
*/
		this.frameAdvance();
		return f;	
	}
	
	
	//not needed?
	public void forceLastFrameOff () {
		
		FrameRecUnit f = this.recording.get(this.recording.size() - 1);
//		f.setCirtfirstisalive(false);
//		f.setCirtsecondisalive(false);
		f.setTouchpts(0);
	}
	

	public void frameAdvance() {
				
		if (this.getCurrentframe() < recording.size() - 1) {
			this.setCurrentframe(getCurrentframe() +1);
		}
		else {
			this.setCurrentframe(0);
			//this.setPlayingback(false);
		}
	}
	
	protected boolean isRecordingnow() {
		return recordingnow;
	}

	protected boolean isPlayingback() {
		return playingback;
	}

	protected void setPlayingback(boolean playingback) {
		this.playingback = playingback;
	}

	protected void setRecordingnow(boolean recordingnow) {
		this.recordingnow = recordingnow;
	}

	protected int getCurrentframe() {
		return currentframe;
	}

	protected void setCurrentframe(int currentframe) {
		this.currentframe = currentframe;
	}

	protected int getMotionevent() {
		return motionevent;
	}

	// もう必要ない
	protected void setMotionevent(int motionevent) {
		this.motionevent = motionevent;
	}

	protected int getTouchpts() {
		return touchpts;
	}

	protected void setTouchpts(int touchpts) {
		
		if(this.isRecordingnow()) {
			this.touchpts = touchpts;	
    	// Log.d("recording", "pts" + touchpts);
		}
	}
}