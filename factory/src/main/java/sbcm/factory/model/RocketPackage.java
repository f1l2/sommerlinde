package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class RocketPackage extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	public RocketPackage() {

	}

	public RocketPackage(int id) {
		super(id, 0, false, 0);
	}

	private Rocket rocket1, rocket2, rocket3, rocket4, rocket5;

	public Rocket getRocket1() {
		return rocket1;
	}

	public void setRocket1(Rocket rocket1) {
		this.rocket1 = rocket1;
	}

	public Rocket getRocket2() {
		return rocket2;
	}

	public void setRocket2(Rocket rocket2) {
		this.rocket2 = rocket2;
	}

	public Rocket getRocket3() {
		return rocket3;
	}

	public void setRocket3(Rocket rocket3) {
		this.rocket3 = rocket3;
	}

	public Rocket getRocket4() {
		return rocket4;
	}

	public void setRocket4(Rocket rocket4) {
		this.rocket4 = rocket4;
	}

	public Rocket getRocket5() {
		return rocket5;
	}

	public void setRocket5(Rocket rocket5) {
		this.rocket5 = rocket5;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
