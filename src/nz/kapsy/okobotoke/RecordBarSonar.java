package nz.kapsy.okobotoke;

import android.util.Log;

public class RecordBarSonar extends RecordBar {

	//private FrameRecorder sonarrec;
	public RecordBarSonar(float swidth, float sheight, long totaltime,
			int frameinterval, FrameRecorder fr, MySurfaceView mysurfv) {
		
		super(swidth, sheight, totaltime, frameinterval, fr, mysurfv);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void initDimensions() {
		this.setRec_left(0F);
		this.setRec_top(this.getTotalheight() - this.getMysurfv().percToPixY(2F));
		
		this.setRec_right(0F);
		this.setRec_bottom(this.getTotalheight() - this.getMysurfv().percToPixY(1F));
	}

	@Override
	public void progressAnim() {
		// int cf = this.getCurrframe();

		if (this.getRec_right() == 0 && this.getFramerec().isRecordingnow()) {
			this.getMysurfv().recsymbol.init();
		}
		if (this.getRec_right() == this.getIncperframe()) {
		}

		if (this.getRec_right() < this.getTotalwidth()) {
			this.incBar();
		} else {
			if (this.getFramerec().isPlayingback()) {
				
			//	this.mysurfv.releaseAllTouchAnims();
				this.getFramerec().startPlayBack();
				this.getMysurfv().playsymbol.init();
				
			} else if (this.getFramerec().isRecordingnow()) {
				Log.d("ProgressAnim", "this.framerec.isRecordingnow() is true");
				
				// タッチ無効する
				//this.getMysurfv().setTouchenabledafterrec(false);
			//	this.framerec.logAllRecordedFrames();
				this.getFramerec().startPlayBack();
				//this.mysurfv.releaseAllTouchAnims();
				this.getMysurfv().setFmrecmode(true);
								
				//大きの方がいい
				this.getMysurfv().playsymbolcntr.init();
			}
			this.init();
		}
	}
}
