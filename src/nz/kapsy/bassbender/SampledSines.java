package nz.kapsy.bassbender;

import android.util.Log;

public class SampledSines {
	
	private static float[] possampsine;
	private static float[] posnegsampsine;
	private static int samples;
	private static float angleinc;
	private static float angle;
	
	public static void init(int s) {
		SampledSines.samples = s;
        angleinc = 360F / (float)samples;
        angle = 0F;
        SampledSines.populatePosSine(samples);
        SampledSines.populatePosNegSine(samples);
    	Log.d("angleinc", "angleinc: " + angleinc);
	}
	
	// 0 to 1 sine, mainly for fades
	public static void populatePosSine(int s) {
		possampsine = new float[samples];
        angle = 0F;
        for (int i = 0; i<samples; i++) {
        	//Log.d("populatePosSine", "sampsineang: " + angle);
        	possampsine[i] = ((((float)Math.sin(Math.toRadians((double)angle))) + 1F) / 2);
        	//Log.d("populatePosSine", "possampledsine[" + i + "]: " + possampsine[i]);
        	angle = angle + angleinc;
        }
	}
	
	public static float getPosSineVal(float angle) {
		outOfRangeAng(angle);
		return possampsine[(int)(angle * (possampsine.length / 360))];
	}
	
	// -1 to 1 sine, for modulation
	public static void populatePosNegSine(int s) {
		posnegsampsine = new float[samples];
        angle = 0F;
        for (int i = 0; i<samples; i++) {

        	//Log.d("populatePosNegSine", "sampsineang: " + angle);
        	posnegsampsine[i] = ((float)Math.sin(Math.toRadians((double)angle)));
        	//Log.d("populatePosNegSine", "posnegsampledsine[" + i + "]: " + posnegsampsine[i]);
        	
        	angle = angle + angleinc;
        }
	}
	
	public static float getPosNegSineVal(float angle) {
		outOfRangeAng(angle);
		return posnegsampsine[(int)(angle * (posnegsampsine.length / 360))];
	}
	
	private static void outOfRangeAng(float angle) {
		
		if (angle < 0F || angle > 360F ) {
				
			Log.d("SampledSines", "Angle " + angle 
					+ "is out of range.");
		}
	}
}