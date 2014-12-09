package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class Propellant extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private int supplierId;

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

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

}
