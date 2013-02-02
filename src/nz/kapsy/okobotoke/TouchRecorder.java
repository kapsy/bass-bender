package nz.kapsy.okobotoke;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import nz.kapsy.okobotoke.MySurfaceView.NormalCircleMultiTouch;
import nz.kapsy.okobotoke.MySurfaceView.NormalLineFader;

import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;

public class TouchRecorder {

	//必要ないかも
	final static int rec_ACTION_DOWN = MotionEvent.ACTION_DOWN;
	final static int rec_ACTION_POINTER_DOWN = MotionEvent.ACTION_POINTER_DOWN;
	final static int rec_ACTION_MOVE = MotionEvent.ACTION_MOVE;
	final static int rec_ACTION_POINTER_UP = MotionEvent.ACTION_POINTER_UP;
	final static int rec_ACTION_UP = MotionEvent.ACTION_UP;

	private final long rectotalmillis = 10000;
	
	private long recstarttime;
private long recfinishtime;

	private boolean recordingnow;

	ArrayList<TouchRecUnit> recording;
	
	private int playbackframe;
	

	
	/**
	 * ここで説明をかけます
	 */
	public TouchRecorder() {
		super();
	
		recording = new ArrayList<TouchRecUnit>();
		
	}
	
	public void startRecording() {
		this.recstarttime = System.currentTimeMillis();
		this.recfinishtime = (this.recstarttime + this.rectotalmillis);
		
		Log.d("startRecording()", "recstarttime " + this.recstarttime 
				+ " recfinishtime " + this.recfinishtime);
		
		this.setRecordingnow(true);
	}
	
	
	public void recordTouchEvent(int ttype,	int tindex, float x, float y) {
		
		if(System.currentTimeMillis() < this.recfinishtime) {
			recording.add(new TouchRecUnit(ttype, tindex, x, y));
			
			TouchRecUnit t = recording.get(recording.size() - 1);
			
			Log.d("recordTouchEvent()", "recording.size(): " + recording.size() 
					+ " time: " + t.getEventtime() + " touch type: " + t.getTouchtype() 
					+ " index: " + t.getIndex() + " x: " + t.getTouch_x() + " y: " + t.getTouch_y());
			
		}
		else {
			this.setRecordingnow(false);
		}
	}

	public void startPlayBack() {Thread playback = 
			new Thread(new Runnable() {
		
			@Override
			public void run() {
				playBack();							
			}
		});
		
	}
	
public void playBack(NormalCircle tt1, NormalCircleMultiTouch txtend, NormalLineFader fline) {
	
	TouchRecUnit t = this.recording.get(this.playbackframe);
	
	if (this.playbackframe < this.recording.size()) {
		
		if (System.currentTimeMillis() == t.getEventtime()) {
			
			switch (t.getTouchtype()) {
			
				case MotionEvent.ACTION_DOWN:
					
					if (t.getIndex() == 0) {

		   	    		txtend.setPosX(t.getTouch_x());
		 	    		txtend.setPosY(t.getTouch_y());
		 	    		
		 	        	txtend.init(0, rndCol(130), rndCol(130), rndCol(130));
		 	        	txtend.setRad(80F);
		 	        		
					}
					
				break;
				
				case MotionEvent.ACTION_POINTER_DOWN:
		    		if (txtend.getRelAnim() == false) {  	
//			        	fline.setLinePoints
//		    			(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
//					    	    	
		    			if (t.getIndex() == 1) {
			    			tt1.setPosX(t.getTouch_x());
				    		tt1.setPosY(t.getTouch_y());
				    	   
		    			}
		    			
			    		//this.faderline.init();	
			    	    
			    		tt1.init(7, rndCol(130), rndCol(130), rndCol(130));
			        	tt1.setRad(80F);
			        	
			        	
			        	
		 	     
		    		}
					
					
					
					break;
				
				case MotionEvent.ACTION_MOVE:
					
		    		if (txtend.getRelAnim() == false) {
		    			if (t.getIndex() == 0) {
			    			txtend.setPosX(t.getTouch_x());
		    	    		txtend.setPosY(t.getTouch_y());
		    			}
//	    	    		OkobotokeActivity.sendFloat("cntr_freq", 
//	    	    				OkobotokeActivity.calcToRangeCentFreq(event.getY(0), this.screenheight));
//	    	    			
//	    	    	
	    	    		
		    		}
					
					
					
					
					break;
					
				case MotionEvent.ACTION_POINTER_UP:
					break;
					
				case MotionEvent.ACTION_UP:
					break;
				
			
			
			
			
			
			}
			
			
				
			
			
			this.playbackframe++;
		}
		
	}
}


	protected boolean isRecordingnow() {
		return recordingnow;
	}

	protected void setRecordingnow(boolean recordingnow) {
		this.recordingnow = recordingnow;
	}
	
	private class TouchRecUnit {
				
		private long eventtime;
		private int touchtype;
		private int index;		
				
		private float touch_x;
		private float touch_y;
		
		/**
		 * @param eventtime
		 * @param touchtype
		 * @param index
		 * @param touch_x
		 * @param touch_y
		 */
		public TouchRecUnit(int touchtype, int index,
				float touch_x, float touch_y) {
			super();
			this.eventtime = System.currentTimeMillis();
			this.touchtype = touchtype;
			this.index = index;
			this.touch_x = touch_x;
			this.touch_y = touch_y;
		}

		protected long getEventtime() {
			return eventtime;
		}

		protected int getTouchtype() {
			return touchtype;
		}

		protected int getIndex() {
			return index;
		}

		protected float getTouch_x() {
			return touch_x;
		}

		protected float getTouch_y() {
			return touch_y;
		}
			
	}
	
	
	
	
	
}

