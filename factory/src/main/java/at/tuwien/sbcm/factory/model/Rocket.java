package at.tuwien.sbcm.factory.model;

import java.io.Serializable;
import java.util.List;

import org.mozartspaces.capi3.Queryable;

@Queryable(autoindex = true)
public class Rocket implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer producerId;

	private Boolean isDefect;

	private Boolean readyForPickUP;

	private Igniter igniter;

	private WoodenStaff woodenStaff;

	private List<Propellant> propellant;

	private List<EffectiveLoad> effectiveLoad;

	public Rocket() {

	}

	public Rocket(Integer id, Integer producerId, Boolean isDefect) {

		this.id = id;
		this.producerId = producerId;
		this.isDefect = isDefect;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProducerId() {
		return producerId;
	}

	public void setProducerId(Integer producerId) {
		this.producerId = producerId;
	}

	public Boolean getReadyForPickUP() {
		return readyForPickUP;
	}

	public void setReadyForPickUP(Boolean readyForPickUP) {
		this.readyForPickUP = readyForPickUP;
	}

	public Boolean getIsDefect() {
		return isDefect;
	}

	public void setIsDefect(Boolean isDefect) {
		this.isDefect = isDefect;
	}

	public Igniter getIgniter() {
		return igniter;
	}

	public void setIgniter(Igniter igniter) {
		this.igniter = igniter;
	}

	public WoodenStaff getWoodenStaff() {
		return woodenStaff;
	}

	public void setWoodenStaff(WoodenStaff woodenStaff) {
		this.woodenStaff = woodenStaff;
	}

	public List<Propellant> getPropellant() {
		return propellant;
	}

	public void setPropellant(List<Propellant> propellant) {
		this.propellant = propellant;
	}

	public List<EffectiveLoad> getEffectiveLoad() {
		return effectiveLoad;
	}

	public void setEffectiveLoad(List<EffectiveLoad> effectiveLoad) {
		this.effectiveLoad = effectiveLoad;
	}

}
