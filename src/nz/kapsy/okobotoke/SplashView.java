package nz.kapsy.okobotoke;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import android.text.style.AlignmentSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class SplashView extends View{

	
	
	Canvas maincanvas = new Canvas();
	
	TextView instructions = new TextView(this.getContext());
	

	Bitmap logo = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.k3_placeholder);
	RectF lrectf;
	
	
	
	protected SplashView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO 自動生成されたコンストラクター・スタブ
		initView();
	}

	protected SplashView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO 自動生成されたコンストラクター・スタブ
		initView();
	}

	protected SplashView(Context context) {
		super(context);
		// TODO 自動生成されたコンストラクター・スタブ
		initView();
	}
	
	
	private void initView() {
		
		

				
		
		this.setBackgroundColor(Color.argb(255, 255, 255, 255));
		
		
		onDraw(maincanvas);
		
	
	}
	
	// screen percentage to pixels
	public float percToPixX(float percent) {
		float pixx = ((float) this.getWidth() / 100F) * percent;
		return pixx;
	}
	
	public float percToPixY(float percent) {
		float pixy = ((float)this.getHeight() / 100F) * percent;
		return pixy;
	}
	
	
	
	
    public void onDraw(Canvas c) {
    	
       // c.drawColor(Color.WHITE);

//        Paint fill_paint = new Paint();
//        fill_paint.setStyle(Paint.Style.FILL);
//        fill_paint.setColor(Color.argb(50, 255, 0, 0));
//        for(int i = 0;i < 10;i++){
//            c.drawOval(new RectF(25f * i, 25f * i, 25f * i + 100f,
//                    25f * i + 100f), fill_paint);
//        }
    	
    	
    	        lrectf = new RectF();
        
        setRecDims(lrectf, 70F);
        //Paint logo_1 = new Paint();
        c.drawBitmap(logo, null, lrectf, null);
        
        
        
        Paint title_1 = new Paint();
//        Typeface tf1;
//        tf1 = Typeface.BOLD;
        
        title_1.setStyle(Paint.Style.FILL);
        title_1.setAntiAlias(true);
        title_1.setColor(Color.argb(255, 255, 0, 0));
        title_1.setTextSize(this.percToPixX(6F));
        title_1.setTextAlign(Align.CENTER);
        title_1.setTypeface(Typeface.create("Ariel", Typeface.ITALIC));
        
        
        
        
        
        
        c.drawText("sonar bass bender", this.percToPixX(50F), this.percToPixY(14F), title_1);
       // c.drawText("bass bend", this.percToPixX(50F), this.percToPixY(18F), title_1);
        c.drawText("touch to continue", this.percToPixX(50F), this.percToPixY(86F), title_1);
        //c.drawText("", this.percToPixX(50F), this.percToPixY(86F), title_1);
        
        
//        c.drawText("@KAPSY198" +
//        		"\nshort tap to sound sonar" +
//        		"\n one finger drag to change pitch" +
//        		"\ntwo finger pinch to growl" +
//        		"\nall movements recorded and looped!" +
//        		"\nheadphones recommended", 100F, 200F, p);
         
    }
    
    public void setRecDims(RectF rect, float widthpercent) {
    	
    	rect.left = (getWidth() / 2F) - percToPixX(widthpercent / 2F); 
    	rect.right = (getWidth() / 2F) + percToPixX(widthpercent / 2F);
    	
		float rheight = rectHeightToBitmapDims((lrectf.right - lrectf.left), logo);
		
		lrectf.top = (getHeight() / 2F) - (rheight / 2F);
		lrectf.bottom = lrectf.top + rheight;
    }
        
    public float rectHeightToBitmapDims(float rwidth, Bitmap bm) {
    	float height = rwidth * ((float) bm.getHeight() / (float) bm.getWidth());
    		return height;
    }
    
    
    
    

}
