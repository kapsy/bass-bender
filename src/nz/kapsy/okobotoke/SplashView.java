package nz.kapsy.okobotoke;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import android.text.style.AlignmentSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class SplashView extends View{

	
	
	Canvas maincanvas = new Canvas();
	
	TextView instructions = new TextView(this.getContext());
	
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
		this.setBackgroundColor(Color.argb(255, 180, 180, 180));
		
		
		onDraw(maincanvas);
		
	
	}
	
	
    public void onDraw(Canvas c) {
    	
       // c.drawColor(Color.WHITE);

        Paint fill_paint = new Paint();
        fill_paint.setStyle(Paint.Style.FILL);
        fill_paint.setColor(Color.argb(50, 255, 0, 0));
        for(int i = 0;i < 10;i++){
            c.drawOval(new RectF(25f * i, 25f * i, 25f * i + 100f,
                    25f * i + 100f), fill_paint);
        }
    
        
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
      //  p.setStrokeWidth(5F);
        p.setAntiAlias(true);
        p.setColor(Color.argb(255, 0, 0, 0));
        
        p.setTextSize(30F);
        
        
        c.drawText("@KAPSY198" +
        		"\nshort tap to sound sonar" +
        		"\n one finger drag to change pitch" +
        		"\ntwo finger pinch to growl" +
        		"\nall movements recorded and looped!" +
        		"\nheadphones recommended", 100F, 200F, p);
        
        
        
        
        
        
    }

}
