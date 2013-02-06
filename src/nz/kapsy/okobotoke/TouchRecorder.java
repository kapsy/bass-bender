package nz.kapsy.okobotoke;

// Please note: this method is now defunct keeping here for ref purposes only.

//ご注意：このクラスは廃止されて使用を推奨されなくなりました。


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

	private final long rectotalmillis = 10000;
	
	private long recstarttime;
private long recfinishtime;

	private boolean recordingnow;

	ArrayList<TouchRecUnit> recording;
	
	private int playbackframe;
	private long playbackstarttime;
	
	private NormalCircle circtouchone;
	private NormalCircleMultiTouch circtouchtwo; 
	private NormalLineFader fline;
	
	public Thread testthr;
	

	
	/**
	 * ここで説明をかきます
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
			recording.add(new TouchRecUnit(ttype, tindex, x, y, this));
			
			TouchRecUnit t = recording.get(recording.size() - 1);
			
			Log.d("playback", "recording.size(): " + recording.size() 
					+ " time: " + t.getEventtime() + " touch type: " + t.getTouchtype() 
					+ " index: " + t.getIndex() + " x: " + t.getTouch_x() + " y: " + t.getTouch_y());
			
		}
		else {
			this.setRecordingnow(false);
		}
	}

	
	public void initPlayBack (NormalCircle circtouchtwo, 
			NormalCircleMultiTouch circtouchone, NormalLineFader fline) {
		
		this.circtouchone = circtouchtwo;
		this.circtouchtwo = circtouchone; 
		this.fline = fline;
		
		this.playbackframe = 0;
		this.playbackstarttime = System.currentTimeMillis();
		
		Log.d("playback", "initPlayBack() called");
		
		

		
		this.startPlayBack();
		
		
		
		
	}
	
	public void startPlayBack() {

					testthr = new Thread( new Runnable() {
			public void run() {
				
				playBack();
				
			}
		});	

		testthr.start();
		
	}
	
	
	
public void playBack() {
	

	
	
	   try
       {
		
	
		   TouchRecUnit t = this.recording.get(this.playbackframe);
			Log.d("playback", "this.playbackframe " + this.playbackframe 
					+ " this.recording.size() " + this.recording.size() );	
           while(this.playbackframe < this.recording.size())
           {

        	   


//	Log.d("playback", "time since play" 
//				+ (System.currentTimeMillis() - this.playbackstarttime));
	testthr.sleep(1);	
	
	long ct = (System.currentTimeMillis() - this.playbackstarttime);
	
		if (ct >= (t.getEventtime() - 10) &&  ct <= (t.getEventtime() + 10)) {
			
			
	   		Log.d("playback", "time since play" 
	   				+ (System.currentTimeMillis() - this.playbackstarttime) 
	   				+ " t.getEventtime() " + t.getEventtime() 
	   				+ " this.playbackframe " + this.playbackframe);	
			
			
			switch (t.getTouchtype()) {
			
				case MotionEvent.ACTION_DOWN:
					
					if (t.getIndex() == 0) {

		   	    		circtouchtwo.setPosX(t.getTouch_x());
		 	    		circtouchtwo.setPosY(t.getTouch_y());
		 	    		
		 	        	//txtend.init(0, rndCol(130), rndCol(130), rndCol(130));
		 	        	circtouchtwo.init(0, 255, 0, 0);
		 	        	circtouchtwo.setRad(80F);
		 	        		
					}
					
				break;
				
				case MotionEvent.ACTION_POINTER_DOWN:
		    		if (circtouchtwo.getRelAnim() == false) {  	
//			        	fline.setLinePoints
//		    			(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
//					    	    	
		    			if (t.getIndex() == 1) {
			    			circtouchone.setPosX(t.getTouch_x());
				    		circtouchone.setPosY(t.getTouch_y());
				    	   
		    			}
		    			
			    		//this.faderline.init();	
			    	    
			    		//tt1.init(7, rndCol(130), rndCol(130), rndCol(130));
			    		circtouchtwo.init(7, 255, 0, 0);
			        	circtouchone.setRad(80F);
			        	
			        	
			        	
		 	     
		    		}
					
					
					
					break;
				
				case MotionEvent.ACTION_MOVE:
					
		    		if (circtouchtwo.getRelAnim() == false) {
		    			
		    			
		    			
		    			if (t.getIndex() == 0) {
			    			circtouchtwo.setPosX(t.getTouch_x());
		    	    		circtouchtwo.setPosY(t.getTouch_y());
		    			}
//	    	    		OkobotokeActivity.sendFloat("cntr_freq", 
//	    	    				OkobotokeActivity.calcToRangeCentFreq(event.getY(0), this.screenheight));
//	    	    			
		    			if (t.getIndex() == 1) {
		    				circtouchone.setPosX(t.getTouch_x());
		    				circtouchone.setPosY(t.getTouch_y());
		    			}
//	    	    	
	    	    		
		    		}
					
					
					
					
					break;
					
				case MotionEvent.ACTION_POINTER_UP:
				
					
	    			
	    			if (t.getIndex() == 1) {
	    				circtouchone.setAlive(false);

	    			}
	    			if (t.getIndex() == 0) {

	    				circtouchone.setAlive(false);
		    			circtouchtwo.relAnimOn();
	    			}
					
						break;
					
				case MotionEvent.ACTION_UP:
				
				
	    			if (t.getIndex() == 0) {

	    				
		    			circtouchtwo.relAnimOn();
	    			}
			
	    			break;
			
			
			}
			
			
				
			
			
			this.playbackframe++;
			t = this.recording.get(this.playbackframe);
			Log.d("playback", "this.playbackframe++");
		}
		
        	   
        	   
        	   
           }
       }
       catch (Exception ex)
       {
           
       }
	
	
	
	
	
	

	
	
	Log.d("playback", "this.playbackframe " + this.playbackframe 
			+ " this.recording.size() " + this.recording.size() );	
	if (this.playbackframe < this.recording.size()) {
		

	}
}


	protected boolean isRecordingnow() {
		return recordingnow;
	}

	protected void setRecordingnow(boolean recordingnow) {
		this.recordingnow = recordingnow;
	}
	
	
	
	
	
	
	
	
	protected long getRecstarttime() {
		return recstarttime;
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
				float touch_x, float touch_y, TouchRecorder rec) {
			super();
			this.eventtime = System.currentTimeMillis() - rec.getRecstarttime();
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

