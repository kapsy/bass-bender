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
		this.setRec_top((float) Math.round(this.getTotalheight()
				- this.getMysurfv().percToPixY(1.6F)));
		this.setRec_right(0F);
		this.setRec_bottom((float) Math.round(this.getTotalheight()
				- this.getMysurfv().percToPixY(0.8F)));
	}

	@Override
	public void progressAnim() {
		// int cf = this.getCurrframe();

		// if (this.getRec_right() == 0 && this.getFramerec().isRecordingnow())
		// {
		// //this.getMysurfv().recsymbol.init();
		// }

		if (this.getRec_right() < this.getTotalwidth()) {
			this.incBar();
		} else {
			if (this.getFramerec().isPlayingback()) {

				this.getFramerec().startPlayBack();

			} else if (this.getFramerec().isRecordingnow()) {
				// Log.d("ProgressAnim",
				// "this.framerec.isRecordingnow() is true");
				this.getFramerec().startPlayBack();
				this.getMysurfv().setFmrecmode(true);

				// 大きの方がいい
				// this.getMysurfv().playsymbolcntr.init();
				this.getMysurfv().playsymbol.init();
			}
			this.init();
		}
	}
}