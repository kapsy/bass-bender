package nz.kapsy.bassbender;
/*
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SplashSurfView extends SurfaceView implements
	SurfaceHolder.Callback {
	
	
	
	private Thread drawthread = null;
	private boolean isattached = false;
	private long timestr = 0, timefin = 0;
	private int threadinterval = 28; //1000/ 28 = 35.714fps
	
	
	private SurfaceHolder holder;
	private Canvas canvas;
	
    private static float screendiag;
    private static float screenwidth;
    private static float screenheight;
	
	public SplashSurfView(Context context) {
        super(context);
        MySurfaceViewInit();
    }

    public SplashSurfView(Context context, AttributeSet attrs) {
        super(context, attrs);
        MySurfaceViewInit();
    }

    public SplashSurfView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        MySurfaceViewInit();
    }

    public void MySurfaceViewInit() {
    	
        //this.setZOrderOnTop(true);

        holder = getHolder();
        holder.setFormat(PixelFormat.OPAQUE);
        holder.addCallback(this);
        
       
       // setFocusable(true);
        //requestFocus();        
    }

    
	
    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		screenwidth = (float) this.getWidth();
		screenheight = (float) this.getHeight();

		if (this.isattached == false) {
			//init
		}

		this.isattached = true;
		startThread();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

    	isattached = false;
    	this.drawthread = null;
	}
	

	public void startThread() {

		// Log.d("Pd Debug", "startThread()");
		this.drawthread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (isattached) {

					timestr = System.currentTimeMillis();

					drawToCanvas();

					timefin = System.currentTimeMillis();

					long diff = timefin - timestr;

					if (diff <= threadinterval) {

						try {
							Thread.sleep(threadinterval - diff);
						} catch (InterruptedException e) {
						}
					} else {
						Log.d("time per cycle", "timefin - timestr: " + diff);
					}
				}
			}
		});

		this.drawthread.start();

	}
	

    
	public void drawToCanvas() {

		canvas = holder.lockCanvas();

		if (canvas != null) {

			this.draw();

			holder.unlockCanvasAndPost(canvas);
		}
	}

	// overidden
	public void draw() {

		//canvas.drawARGB(155, 255, 0, 0);
		Paint p = new Paint();
		
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.RED);
        p.setAntiAlias(false);
        p.setDither(false);		
        p.setTextSize(100F);
		
		canvas.drawText("KAPSY", 100F, 100F, p);
		
		
	}
	
//	public float widthRatioToPixels() {
//	
//		
//		
//	}
//	
//	public float heightRatioToPixels() {
//		
//	}
	
	
	
	
	
	
	
}
*/