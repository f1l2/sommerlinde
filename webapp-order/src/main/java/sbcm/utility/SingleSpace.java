package sbcm.utility;

import sbc.space.*;

public class SingleSpace {
	private static volatile SingleSpace instance = null;

	public final static String URI = "localhost:1235";

	private AlterSpaceServer shippingSpace;

	private SingleSpace() {
		this.shippingSpace = new AlterSpaceServer(1235);
		this.shippingSpace.init();
	}

	public AlterSpaceServer getShippingSpace() {
		return shippingSpace;
	}

	public void setShippingSpace(AlterSpaceServer shippingSpace) {
		this.shippingSpace = shippingSpace;
	}

	public static SingleSpace getInstance() {
		if (instance == null) {
			synchronized (SingleSpace.class) {
				if (instance == null) {
					instance = new SingleSpace();
				}
			}
		}

		return instance;
	}
}
