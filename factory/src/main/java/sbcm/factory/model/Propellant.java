package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class Propellant extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private Employee supplier;

	private Integer amount;

	public Propellant() {
	}

	public Propellant(int id) {
		super(id, 0, false, 0);
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Employee getSupplier() {
		return supplier;
	}

	public void setSupplier(Employee supplier) {
		this.supplier = supplier;
	}

}
