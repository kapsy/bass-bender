package nz.kapsy.bassbender;
//
////Please note: this method is now defunct keeping here for ref purposes only.
//
////ご注意：このクラスは廃止されて使用を推奨されなくなりました。
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//
//public class TouchView extends View {
//	
//	MySurfaceView mysurfaceview;
//	FrameRecorder framerec;
//
//	protected TouchView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//	}
//
//	protected TouchView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//	}
//
//	protected TouchView(Context context) {
//		super(context);
//	}
//	
//	public void init(MySurfaceView mysurfaceview, FrameRecorder framerec){
//		this.mysurfaceview = mysurfaceview; 
//		this.framerec = framerec;
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//
//		MySurfaceView m = this.mysurfaceview;
//    	int pts = event.getPointerCount();
//   		framerec.setTouchpts(pts);
//    	
//    	int index = event.getActionIndex();
////    	Log.v("MotionEvent", "event.getActionIndex() " + event.getActionIndex());
//
//		 switch (event.getAction() & MotionEvent.ACTION_MASK) {
//		 
//		 	case MotionEvent.ACTION_DOWN:
////	 	        	Log.v("MotionEvent", "ACTION_DOWN");
//    			 
//   	    		m.circtouchfirst.setPosX(event.getX(0));
// 	    		m.circtouchfirst.setPosY(event.getY(0));
// 	        	m.circtouchfirst.init(0, m.rndCol(130), m.rndCol(130), m.rndCol(130));
// 	        	m.circtouchfirst.initRad(80F);
// 	        	framerec.setMotionevent(MotionEvent.ACTION_DOWN);
// 	        	
// 	        	break;
//			 
//	 
//	        case MotionEvent.ACTION_POINTER_DOWN:
////    	        	Log.v("MotionEvent", "ACTION_POINTER_DOWN");
//	        	// ここで必要ないかも
//	    		if (m.circtouchfirst.getRelAnim() == false) {  	
//		        	m.faderline.setLinePoints
//	    			(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
//				    	    			
//	    			m.circtouchsecond.setPosX(event.getX(1));
//		    		m.circtouchsecond.setPosY(event.getY(1));
//		    	   
//		    		m.faderline.init();	
//		    	    
//		    		m.circtouchsecond.init(7, m.rndCol(130), m.rndCol(130), m.rndCol(130));
//		        	m.circtouchsecond.initRad(80F);
//		        	
//	 	        	framerec.setMotionevent(MotionEvent.ACTION_POINTER_DOWN);
//	    		}
//	    		break;
//	        	
//	    	case MotionEvent.ACTION_MOVE:
////    	    		Log.v("MotionEvent", "ACTION_MOVE");
//	    		
//	    		if (m.circtouchfirst.getRelAnim() == false) {
//	    			m.circtouchfirst.setPosX(event.getX(0));
//    	    		m.circtouchfirst.setPosY(event.getY(0));
//	    	    	    			
//    	    		BassBenderActivity.sendFloat("cntr_freq", 
//    	    				BassBenderActivity.calcToRangeCentFreq(event.getY(0), this.getHeight()));
//    	    		 	    
//	 	        	framerec.setMotionevent(MotionEvent.ACTION_MOVE);
//	    	
//		    		if (pts == MySurfaceView.getMultiTouchMax()) {
////		    			if (oneInTwo()) {
//	    	    			
//	    	    			m.faderline.setLinePoints
//		    	    			(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
//	    	    		
//	    	    			m.circtouchsecond.setPosX(event.getX(1));
//		    	    		m.circtouchsecond.setPosY(event.getY(1));
//		    	    				    	    		
////		    	    		if (oneInFour()) {
//		    	    			
//		    	    			BassBenderActivity.sendFloat("fm_index", BassBenderActivity.calcToRangeFM(
//		    	    			m.faderline.calcDistance(), m.getScreendiag()));
//		    		}
//	    		}	    		    	    		
//	    		break;
//    		
//	    	case MotionEvent.ACTION_POINTER_UP:
////    	    		Log.v("MotionEvent", "ACTION_POINTER_UP");
//
//	    		if (index == 1) {
//	    			
//		    		BassBenderActivity.sendFloat("fm_index", 12F);
//	    	    	m.circtouchsecond.setAlive(false);
//	    	    	m.faderline.setAlive(false);
//	 	       
//	 	       		framerec.setMotionevent(MotionEvent.ACTION_POINTER_UP);
//	    		}
//	    		
//   	    		if (index == 0) {
//   	    			
//       	 			BassBenderActivity.sendFloat("fm_index", 12F);
//       				m.circtouchsecond.setAlive(false);
//       				m.circtouchfirst.relAnimOn();
//       				m.faderline.setAlive(false);
// 	 	       
//	 	       		framerec.setMotionevent(MotionEvent.ACTION_POINTER_UP);
//	    		}
//	    		break;
//	    		
//	    	case MotionEvent.ACTION_UP:
////    	    		Log.v("MotionEvent", "ACTION_UP");
//	    		
//	    		if (index == 0) {
//	    			m.circtouchfirst.relAnimOn();
//	 	       		framerec.setMotionevent(MotionEvent.ACTION_UP);
//	 	      		framerec.setTouchpts(0);
//	    		}
//	    		break;
//		 }
//    		 
//		// 指三本処理
//    	if (pts > MySurfaceView.getMultiTouchMax()) {
//			BassBenderActivity.sendFloat("fm_index", 12F);
//			m.circtouchsecond.setAlive(false);
//			m.circtouchfirst.relAnimOn();
//			m.faderline.setAlive(false);
//    	}
//		return true;
//	}
//}
