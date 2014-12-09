package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class Propellant extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	public Propellant() {

	}

	public Propellant(int id) {
		super(id, 0, false, 0);
	}

	private Integer amount;

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

}
