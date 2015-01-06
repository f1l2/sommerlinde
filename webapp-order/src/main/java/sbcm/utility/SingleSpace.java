package sbcm.utility;

import sbc.space.MozartSpaces;

public class SingleSpace {
	private static volatile SingleSpace instance = null;

	public final static String URI = "xvsm://localhost:9874/";

	private MozartSpaces shippingSpace;

	private SingleSpace() {
		this.shippingSpace = new MozartSpaces(Boolean.TRUE, URI);
		this.shippingSpace.init();
	}

	public MozartSpaces getShippingSpace() {
		return shippingSpace;
	}

	public void setShippingSpace(MozartSpaces shippingSpace) {
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
