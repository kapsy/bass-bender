package nz.kapsy.okobotoke;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

public class NormalLine extends NormalCircle {

	private float[] linepoints = new float[4];
	
/*	@Override
	public void init() {
		// setPos メソッドの方がいいかも???
//		posx = (float)rnd.nextInt(getWidth());
//		posy = (float)rnd.nextInt(getHeight());
		
		//rad = 20;
		//alpha = 100;
		alphaslower = 0F;
		red = 255 - rnd.nextInt(130);
		grn = 255 - rnd.nextInt(130);
		blu = 255 - rnd.nextInt(130);
		
		//alphaslower = 0;
		
//		rspd = (float)45;
//		yspd = (float)0.674375;
		
		frame = 1;
		
		alive = true;
		
		//initbackground = false;
		
		
//		sinangle = 0;
//		sinanglechangerate = (float)4.5;
//		modamplitude = (float).8;
		
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth((float)(150 - rnd.nextInt(100)));
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setColor(Color.argb(150, red, grn, blu));
	}
	*/
	
	@Override
	public void drawSequence(Canvas c) {
		if (this.isAlive()) {
			//this.circleAnim();
			//this.circleRadiusMod();
			this.drawLineOnce(c);
		}
	}
	
	public void drawLineOnce(Canvas c) {
		
    	this.getPaint().setColor(Color.argb
    			(this.getAlpha(), this.getRed(), this.getGrn(), this.getBlu()));
        
		c.drawLine(linepoints[0], linepoints[1], linepoints[2], linepoints[3], this.getPaint());
		
//        Log.d("drawCircleOnce", "Colors " + "a " + alpha + " r " + red + " g " + grn+ " b " + blu);
//        Log.d("drawCircleOnce", "Dimens " + "x " + posx + " y " + posy + " r " + rad);

}
	
	
	public void setLinePoints(float x1, float y1, float x2, float y2) {
		linepoints[0] = x1;
		linepoints[1] = y1;
		linepoints[2] = x2;
		linepoints[3] = y2;

	}
	
	public float calcDistance() {
		
		float x1 = this.linepoints[0];
		float x2 = this.linepoints[2];
		float y1 = this.linepoints[1];
		float y2 = this.linepoints[3];
		
		double xdiff = (double)Math.abs(x1 - x2);
		double ydiff = (double)Math.abs(y1 - y2);
		
	    float dist = (float)Math.sqrt(Math.pow(xdiff, 2D) + Math.pow(ydiff, 2D));
		
	   // Log.d("calcDistance", "dist " + dist);
	    return dist;

	}
	
}
