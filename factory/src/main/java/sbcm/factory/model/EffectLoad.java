package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class EffectLoad extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private int supplierId;

	private Boolean isDefect;

	public EffectLoad() {
	}

	public EffectLoad(int id) {
		super(id, 0, false, 0);
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public Boolean getIsDefect() {
		return isDefect;
	}

	public void setIsDefect(Boolean isDefect) {
		this.isDefect = isDefect;
	}

}
