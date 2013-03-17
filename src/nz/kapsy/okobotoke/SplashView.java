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
	

	Bitmap logo = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.k3_placeholder_3);
	RectF lrectf;
	
	private static float width = 0F;
	private static float height = 0F;
	
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
		
		this.setBackgroundColor(Color.argb(255, 0, 140, 255));
		onDraw(maincanvas);
	}
	// screen percentage to pixels
	public static float percToPixX(float percent) {
		float pixx = ( width / 100F) * percent;
		return pixx;
	}
	
	public static float percToPixY(float percent) {
		float pixy = ( height / 100F) * percent;
		return pixy;
	}
	
	public static float percToPixX(float width, float percent) {
		float pixx = ( width / 100F) * percent;
		return pixx;
	}
	
	public static float percToPixY(float height, float percent) {
		float pixy = ( height / 100F) * percent;
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
    	
    	
		width = this.getWidth();
		height = this.getHeight();
    	
    	
    	        lrectf = new RectF();
        
        setRecDims(lrectf, 70F);
        //Paint logo_1 = new Paint();
        c.drawBitmap(logo, null, lrectf, null);
                
        Paint title_1 = new Paint();
        title_1.setStyle(Paint.Style.FILL);
        title_1.setAntiAlias(true);
        title_1.setColor(Color.argb(255, 0, 255, 255));
        title_1.setTextSize(percToPixX(12F));
        title_1.setTextAlign(Align.CENTER);
        title_1.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC));
        
        Paint question_1 = new Paint();
        question_1.setStyle(Paint.Style.FILL);
        question_1.setAntiAlias(true);
        question_1.setColor(Color.argb(255, 255, 230, 230));
        question_1.setTextSize(percToPixX(16F));
        question_1.setTextAlign(Align.RIGHT);
        question_1.setTypeface(Typeface.create("Ariel", Typeface.NORMAL));
        
        c.drawText("bass bender~", percToPixX(48F), percToPixY(16F), title_1);
        
 title_1.setTextSize(percToPixX(3F));
        title_1.setTypeface(Typeface.create("Ariel", Typeface.ITALIC));
        c.drawText("by", percToPixX(16F), percToPixY(27F), title_1);
        
        
        
        title_1.setTextSize(percToPixX(4F));
        title_1.setTypeface(Typeface.create("Ariel", Typeface.NORMAL));
        title_1.setColor(Color.argb(255, 0, 255, 255));
        
        
        c.drawText("built for headphones / speakers", percToPixX(52F), percToPixY(80F), title_1);
       
        title_1.setTextSize(percToPixX(5F));
        title_1.setTypeface(Typeface.create("Ariel", Typeface.NORMAL));
        title_1.setColor(Color.argb(255, 0, 255, 0));
        
        c.drawText("t o u c h    t o    continue...", percToPixX(52F), percToPixY(90F), title_1);

        title_1.setTextSize(percToPixX(2.5F));
        title_1.setTypeface(Typeface.create("Ariel", Typeface.ITALIC));
        title_1.setColor(Color.argb(255, 0, 40, 255));
        c.drawText("powered by libpd.    contact: www.kapsy198.com", percToPixX(42F), percToPixY(97F), title_1);

        
        
        
        c.drawText("?", (float) getWidth() - percToPixX(5F), (float) getHeight() - percToPixX(5F), question_1);
    }
    
    // calcs the rec to same ratio as bitmap
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
