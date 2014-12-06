package sbc.space;

import org.mozartspaces.core.ContainerReference;

public class MozartContainer extends Container {
	private ContainerReference container;

	public MozartContainer(ContainerReference c) {
		container = c;
	}

	public ContainerReference getContainer() {
		return container;
	}
}
