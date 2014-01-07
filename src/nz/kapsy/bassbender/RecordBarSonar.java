package nz.kapsy.bassbender;

import android.util.Log;

public class RecordBarSonar extends RecordBar {

	public RecordBarSonar(float swidth, float sheight, long totaltime,
			int frameinterval, FrameRecorder fr, MySurfaceView mysurfv) {
		
		super(swidth, sheight, totaltime, frameinterval, fr, mysurfv);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void initDimensions() {
		this.setRec_left(0F);
		this.setRec_top((float) Math.round(this.getTotalheight()
				- this.getMysurfv().percToPixY(1.6F)));
		this.setRec_right(0F);
		this.setRec_bottom((float) Math.round(this.getTotalheight()
				- this.getMysurfv().percToPixY(0.8F)));
	}

	@Override
	public void progressAnim() {

		if (this.getRec_right() < this.getTotalwidth()) {
			this.incBar();
		} else {
			
			if (this.getFramerec().isPlayingback()) {
				this.getFramerec().startPlayBack();
				
			} else if (this.getFramerec().isRecordingnow()) {
				this.getFramerec().startPlayBack();
				this.getMysurfv().setFmrecmode(true);
				this.getMysurfv().playsymbol.init();
			}
			
			this.init();
		}
	}
}