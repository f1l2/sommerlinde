package sbcm.factory.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.Queryable;

@Queryable(autoindex = true)
public class Rocket implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private List<Integer> employee;

	private Boolean isDefect;

	private Boolean readyForPickUP;

	private Igniter igniter;

	private WoodenStaff woodenStaff;

	private List<Propellant> propellant;

	private List<EffectiveLoad> effectiveLoad;

	private Integer fillingQuantity;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Rocket() {

		this.employee = new ArrayList<Integer>();
	}

	public Rocket(Integer id, Integer employeeId, Boolean isDefect) {

		this.id = id;
		this.isDefect = isDefect;

		this.employee = new ArrayList<Integer>();
		if (employeeId != null)
			this.employee.add(employeeId);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getFillingQuantity() {
		return fillingQuantity;
	}

	public void setFillingQuantity(Integer fillingQuantity) {
		this.fillingQuantity = fillingQuantity;
	}

	public List<Integer> getEmployee() {
		return employee;
	}

	public void setEmployee(List<Integer> employee) {
		this.employee = employee;
	}

	public void addEmployee(Integer employeeId) {
		this.employee.add(employeeId);
	}

}
