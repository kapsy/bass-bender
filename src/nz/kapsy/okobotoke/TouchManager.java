package nz.kapsy.okobotoke;

import android.view.MotionEvent;

public class TouchManager {
/*
	 @Override
	    public boolean onTouchEvent(MotionEvent event) {
	    	
	    	

	    	
	    	int pts = event.getPointerCount();
	    	
	    	int index = event.getActionIndex();
//	    	Log.v("MotionEvent", "event.getActionIndex() " + event.getActionIndex());

			 switch (event.getAction() & MotionEvent.ACTION_MASK) {
			 
			 	case MotionEvent.ACTION_DOWN:
//		 	        	Log.v("MotionEvent", "ACTION_DOWN");
	    			 
	   	    		this.circtouchfirst.setPosX(event.getX(0));
	 	    		this.circtouchfirst.setPosY(event.getY(0));
	 	    		
	 	        	this.circtouchfirst.init(0, rndCol(130), rndCol(130), rndCol(130));
	 	        	this.circtouchfirst.setRad(80F);
	 	        	
	 	        	

	 	        	
	 	        	break;
				 
		 
		        case MotionEvent.ACTION_POINTER_DOWN:
//	    	        	Log.v("MotionEvent", "ACTION_POINTER_DOWN");
		        	// ここで必要ないかも
		    		if (this.circtouchfirst.getRelAnim() == false) {  	
			        	this.faderline.setLinePoints
		    			(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
					    	    			
		    			this.circtouchsecond.setPosX(event.getX(1));
			    		this.circtouchsecond.setPosY(event.getY(1));
			    	   
			    		this.faderline.init();	
			    	    
			    		this.circtouchsecond.init(7, rndCol(130), rndCol(130), rndCol(130));
			        	this.circtouchsecond.setRad(80F);
			        	

			        	
		    		}
		    		break;
		        	
		    	case MotionEvent.ACTION_MOVE:
//	    	    		Log.v("MotionEvent", "ACTION_MOVE");
		    		
		    		if (this.circtouchfirst.getRelAnim() == false) {
		    			this.circtouchfirst.setPosX(event.getX(0));
	    	    		this.circtouchfirst.setPosY(event.getY(0));
		    	    	    			
	    	    		OkobotokeActivity.sendFloat("cntr_freq", 
	    	    				OkobotokeActivity.calcToRangeCentFreq(event.getY(0), this.screenheight));
	    	    			

		    	
		    		if (pts == MULTI_TOUCH_MAX) {
		    			if (oneInTwo()) {
	    	    			
	    	    			this.faderline.setLinePoints
		    	    			(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
	    	    		
	    	    			this.circtouchsecond.setPosX(event.getX(1));
		    	    		this.circtouchsecond.setPosY(event.getY(1));
		    	    				    	    		
		    	    		if (oneInFour()) {
		    	    			
		    	    			OkobotokeActivity.sendFloat("fm_index", OkobotokeActivity.calcToRangeFM(
		    	    			this.faderline.calcDistance(), screendiag));
		    	    			
//		    	    			Log.d("sendFloat", "fm_index " + "faderline.calcDistance() " 
//	    	    					+ this.faderline.calcDistance() + " screendiag " + screendiag + " calcToRangeFM " + OkobotokeActivity.calcToRangeFM(
//	    	    							this.faderline.calcDistance(), screendiag)); 
		    	    		}
	    	    		}
		    			

		    			
		    		}
		    		}	    		    	    		
		    		break;
	    		
		    	case MotionEvent.ACTION_POINTER_UP:
		    		// フェードアウト・アニメーション
//	    	    		Log.v("MotionEvent", "ACTION_POINTER_UP");

		    		if (index == 1) {
		    			
			    		OkobotokeActivity.sendFloat("fm_index", 12F);
		    	    	this.circtouchsecond.setAlive(false);
		    	    	this.faderline.setAlive(false);
		    	    	

	    	    	
		    	    	
		    		}
		    		
	   	    		if (index == 0) {
	   	    			
	       	 			OkobotokeActivity.sendFloat("fm_index", 12F);
	       				this.circtouchsecond.setAlive(false);
	       				this.circtouchfirst.relAnimOn();
	       				this.faderline.setAlive(false);
	
	       				
		    		}
		    		
		    		break;
		    		
		    	case MotionEvent.ACTION_UP:
//	    	    		Log.v("MotionEvent", "ACTION_UP");
		    		
		    		if (index == 0) {
		    			this.circtouchfirst.relAnimOn();

		    			
		    		}
		    			    	    		
		    		break;

			 }
	    		 
	    	if (pts > MULTI_TOUCH_MAX) {
	    		
				OkobotokeActivity.sendFloat("fm_index", 12F);
				this.circtouchsecond.setAlive(false);
				this.circtouchfirst.relAnimOn();
				
				this.faderline.setAlive(false);
	    	}
	    	
	    	
//			invalidate();
			return true;
	        //return super.onTouchEvent(event);
	    }
	
	
	*/
	
}
