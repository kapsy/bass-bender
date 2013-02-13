package nz.kapsy.okobotoke;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SplashView extends View{

	
	
	Canvas maincanvas = new Canvas();
	
	
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
		this.setBackgroundColor(Color.DKGRAY);
		
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
        p.setColor(Color.argb(50, 255, 0, 0));
        
        p.setTextSize(100F);
        
        c.drawText("KAPSY", 200F, 200F, p);
        
        
        
        
        
        
    }

}
