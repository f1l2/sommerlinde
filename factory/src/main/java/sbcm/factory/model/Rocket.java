package sbcm.factory.model;

import java.io.Serializable;
import java.util.List;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class Rocket extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Employee> employee;

	private Boolean isDefect;

	private Igniter igniter;

	private WoodenStaff woodenStaff;

	private List<Propellant> propellant;

	private List<EffectLoad> effectiveLoad;

	private Integer fillingQuantity;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Rocket() {
	}

	public Rocket(Integer id, Integer employeeId, Boolean isDefect) {

		super(id, 0, false, 0);

		this.isDefect = isDefect;

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

	public List<EffectLoad> getEffectiveLoad() {
		return effectiveLoad;
	}

	public void setEffectiveLoad(List<EffectLoad> effectiveLoad) {
		this.effectiveLoad = effectiveLoad;
	}

	public Integer getFillingQuantity() {
		return fillingQuantity;
	}

	public void setFillingQuantity(Integer fillingQuantity) {
		this.fillingQuantity = fillingQuantity;
	}

	public List<Employee> getEmployee() {
		return employee;
	}

	public void setEmployee(List<Employee> employee) {
		this.employee = employee;
	}

}
