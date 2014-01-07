package nz.kapsy.bassbender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class InfoView extends SplashView{

	protected InfoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO 自動生成されたコンストラクター・スタブ
		//initView();
	}

	protected InfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO 自動生成されたコンストラクター・スタブ
		//initView();
	}

	protected InfoView(Context context) {
		super(context);
		// TODO 自動生成されたコンストラクター・スタブ
		//initView();
	}
	
	private void initView() {
		onDraw(maincanvas);
	}
	
    public void onDraw(Canvas c) {

    	this.setBackgroundColor(Color.argb(255, 80, 0, 127));
    	lrectf = new RectF();
        setRecDims(lrectf, 70F);
        
        Paint title_1 = new Paint();
        title_1.setStyle(Paint.Style.FILL);
        title_1.setAntiAlias(true);
        title_1.setColor(Color.argb(255, 0, 255, 0));
        title_1.setTextSize(percToPixX(5F));
        title_1.setTextAlign(Align.CENTER);
        title_1.setTypeface(Typeface.create("Ariel", Typeface.NORMAL));
        
        Paint normal_1 = new Paint();
        normal_1.setStyle(Paint.Style.FILL);
        normal_1.setAntiAlias(true);
        normal_1.setColor(Color.argb(255, 0, 255, 0));
        normal_1.setTextSize(percToPixX(3F));
        normal_1.setTextAlign(Align.CENTER);
        normal_1.setTypeface(Typeface.create("Ariel", Typeface.NORMAL));
        
        float info = 10F;
        float infosp = 6F;
        
        float hiw = 33F; 
        float hiwsp = 6F;

        title_1.setTypeface(Typeface.create("Ariel", Typeface.ITALIC));
        c.drawText("the aim...", 
        		percToPixX(50F), percToPixY(info), title_1);
        c.drawText("bass bender is a quest to discover alternative methods of", 
        		percToPixX(50F), percToPixY(info + (infosp * 1F)), normal_1);
        c.drawText("instrument control, that are more intuative and feel based,", 
        		percToPixX(50F), percToPixY(info + (infosp * 1.5F)), normal_1);
        c.drawText("and less number and knob based.", 
        		percToPixX(50F), percToPixY(info + (infosp * 2F)), normal_1);
        
        title_1.setColor(Color.argb(255, 0, 255, 255));
        normal_1.setColor(Color.argb(255, 0, 255, 255));
        
        title_1.setTypeface(Typeface.create("Ariel", Typeface.NORMAL));
        c.drawText("how it works", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 0F)), title_1);
        
        c.drawText("the synth continuously cycles through a set sequence of notes.", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 1F)), normal_1);
        c.drawText("all touches are recorded over 25.0 seconds and then looped back. ", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 2F)), normal_1);
        c.drawText("further touches will start the recording again.", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 3F)), normal_1);
        
        c.drawText("first finger touch: ", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 4F)), normal_1);
        c.drawText("x axis to pitchbend between notes. ", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 4.5F)), normal_1);
        c.drawText("y axis to change the overall pitch of the synth.", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 5F)), normal_1);
        
        c.drawText("second finger:", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 6F)), normal_1);
        c.drawText("creates a fader that 'growls' the sound based on its length.", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 6.5F)), normal_1);
        c.drawText("because the sounds created are of very low frequencies,", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 7.5F)), normal_1);
        c.drawText("headphones or full range speakers are recommended.", 
        		percToPixX(50F), percToPixY(hiw + (hiwsp * 8F)), normal_1);

        title_1.setColor(Color.argb(255, 255, 255, 0));

        title_1.setTextSize(percToPixX(4F));
        c.drawText("t o u c h to play...", percToPixX(50F), percToPixY(92F), title_1);
    }
}