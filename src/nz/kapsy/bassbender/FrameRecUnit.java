package nz.kapsy.bassbender;

public class FrameRecUnit {
	

	private float cirtfirstx;
	private float cirtfirsty;
	
	private float cirtsecondx;
	private float cirtsecondy;
	
	private int motionevent;
	private int touchpts;
	private int index;
	
	/**
	 * @param cirtfirstisalive
	 * @param cirtsecondisalive
	 * @param cirtfirstx
	 * @param cirtfirsty
	 * @param cirtsecondx
	 * @param cirtsecondy
	 */
	
	protected FrameRecUnit(
			float cirtfirstx, float cirtfirsty,
			float cirtsecondx, float cirtsecondy, 
			int motionevent, int touchpts, int index) {
		super();
		this.cirtfirstx = cirtfirstx;
		this.cirtfirsty = cirtfirsty;			

		this.cirtsecondx = cirtsecondx;
		this.cirtsecondy = cirtsecondy;
		
		this.motionevent = motionevent;
		this.touchpts = touchpts;
		this.index = index;
	}

	protected float getCirtfirstx() {
		return cirtfirstx;
	}

	protected float getCirtfirsty() {
		return cirtfirsty;
	}

	protected float getCirtsecondx() {
		return cirtsecondx;
	}

	protected float getCirtsecondy() {
		return cirtsecondy;
	}

	protected void setCirtfirstx(float cirtfirstx) {
		this.cirtfirstx = cirtfirstx;
	}

	protected void setCirtfirsty(float cirtfirsty) {
		this.cirtfirsty = cirtfirsty;
	}

	protected void setCirtsecondx(float cirtsecondx) {
		this.cirtsecondx = cirtsecondx;
	}

	protected void setCirtsecondy(float cirtsecondy) {
		this.cirtsecondy = cirtsecondy;
	}

	protected Integer getMotionevent() {
		return motionevent;
	}

	protected void setMotionevent(int motionevent) {
		this.motionevent = motionevent;
	}

	protected int getTouchpts() {
		return touchpts;
	}

	protected void setTouchpts(int touchpts) {
		this.touchpts = touchpts;
	}

	protected int getIndex() {
		return index;
	}

	protected void setIndex(int index) {
		this.index = index;
	}
}
