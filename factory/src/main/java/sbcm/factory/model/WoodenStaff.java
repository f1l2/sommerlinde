package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class WoodenStaff extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	public WoodenStaff() {

	}

	public WoodenStaff(int id) {
		super(id, 0, false, 0);
	}

}
