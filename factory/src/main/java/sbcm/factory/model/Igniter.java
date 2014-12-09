package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class Igniter extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private Employee supplier;

	public Igniter() {

	}

	public Igniter(int id) {
		super(id, 0, false, 0);
	}

	public Employee getSupplier() {
		return supplier;
	}

	public void setSupplier(Employee supplier) {
		this.supplier = supplier;
	}
}
