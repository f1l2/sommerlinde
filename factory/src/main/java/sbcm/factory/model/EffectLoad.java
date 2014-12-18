package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class EffectLoad extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private Employee supplier;

	private Boolean isDefect;
	
	private EffectLoadColor color;

	public EffectLoad() {
	}

	public EffectLoad(int id) {
		super(id, 0, false, 0);
	}

	public Boolean getIsDefect() {
		return isDefect;
	}

	public void setIsDefect(Boolean isDefect) {
		this.isDefect = isDefect;
	}

	public Employee getSupplier() {
		return supplier;
	}

	public void setSupplier(Employee supplier) {
		this.supplier = supplier;
	}

	public EffectLoadColor getColor() {
		return color;
	}

	public void setColor(EffectLoadColor color) {
		this.color = color;
	}

}
