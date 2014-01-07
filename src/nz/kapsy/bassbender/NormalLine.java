package nz.kapsy.bassbender;

import nz.kapsy.bassbender.MySurfaceView.AccelTouch;
import android.graphics.Canvas;
import android.graphics.Color;

public class NormalLine extends NormalCircle {

	private float[] linepoints = new float[4];
	
	// snagpoints used to ensure line is always attached to something
	// therefore a 'disconnect' between line and circle cannot occur
	private AccelTouch snagpoint1;
	private AccelTouch snagpoint2;
	
	@Override
	public void drawSequence(Canvas c) {
		if (this.isAlive()) {
			this.drawLineOnce(c);
		}
	}
	
	public void drawLineOnce(Canvas c) {

		this.getPaint().setColor(
				Color.argb(this.getAlpha(), this.getRed(), this.getGrn(),
						this.getBlu()));
		
		c.drawLine(linepoints[0], linepoints[1], linepoints[2], linepoints[3],
				this.getPaint());
	}
	
	public void setLinePoints(float x1, float y1, float x2, float y2) {
		linepoints[0] = x1;
		linepoints[1] = y1;
		linepoints[2] = x2;
		linepoints[3] = y2;
	}
	
	public void setLineBySnags() {
		if (snagpoint1 != null && snagpoint2 != null) {
			linepoints[0] = snagpoint1.getPosX();
			linepoints[1] = snagpoint1.getPosY();
			linepoints[2] = snagpoint2.getPosX();
			linepoints[3] = snagpoint2.getPosY();
		}
	}
	
	public float calcDistance() {
		
		float x1 = this.linepoints[0];
		float x2 = this.linepoints[2];
		float y1 = this.linepoints[1];
		float y2 = this.linepoints[3];
		
		double xdiff = (double)Math.abs(x1 - x2);
		double ydiff = (double)Math.abs(y1 - y2);
		
	    float dist = (float)Math.sqrt(Math.pow(xdiff, 2D) + Math.pow(ydiff, 2D));
		
	    return dist;

	}

	protected AccelTouch getSnagpoint1() {
		return snagpoint1;
	}

	protected AccelTouch getSnagpoint2() {
		return snagpoint2;
	}

	protected void setSnagpoint1(AccelTouch snagpoint1) {
		this.snagpoint1 = snagpoint1;
	}

	protected void setSnagpoint2(AccelTouch snagpoint2) {
		this.snagpoint2 = snagpoint2;
	}

	protected float[] getLinepoints() {
		return linepoints;
	}
}