package sbc.space;

import org.mozartspaces.capi3.Selector;

public class MozartSelector extends SpaceSelector {

	private Selector selector;

	public MozartSelector(Selector selector) {
		super();
		this.selector = selector;
	}

	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

}
