package sbcm.supplier;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;

import sbc.space.MozartSpaces;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.WoodenStaff;
import sbcm.space.role.Role;

@ManagedBean(name = "supplier")
@ViewScoped
public class Supplier extends Role {

	private String name;
	private Integer woodenstaff;
	private Integer igniter;
	private Integer propellant;
	private Integer effectLoad;
	private Integer errorRate;

	public int getProducerId() {
		return employeeId;
	}

	public void setProducerId(int producerId) {
		this.employeeId = producerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getWoodenstaff() {
		return woodenstaff;
	}

	public void setWoodenstaff(Integer woodenstaff) {
		this.woodenstaff = woodenstaff;
	}

	public Integer getIgniter() {
		return igniter;
	}

	public void setIgniter(Integer igniter) {
		this.igniter = igniter;
	}

	public Integer getPropellant() {
		return propellant;
	}

	public void setPropellant(Integer propellant) {
		this.propellant = propellant;
	}

	public Integer getEffectLoad() {
		return effectLoad;
	}

	public void setEffectLoad(Integer effectLoad) {
		this.effectLoad = effectLoad;
	}

	public Integer getErrorRate() {
		return errorRate;
	}

	public void setErrorRate(Integer errorRate) {
		this.errorRate = errorRate;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void main(String[] args) {

		new Supplier();

		System.exit(0);
	}

	public Supplier() {
		super();
	}

	@Override
	protected void doAction() {

	}

	public void deliver() {
		logger.info(this.employeeId + " started.");

		for (int i = 1; i <= this.igniter; i++) {

			Igniter ig = new Igniter(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
			try {
				mozartSpaces.write(MozartSpaces.PARTS, ig);
			} catch (Exception e) {
				logger.error("Write igniter exception", e);
			}
		}

		for (int i = 1; i <= this.propellant; i++) {

			Propellant p = new Propellant(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
			p.setAmount(500);

			try {
				mozartSpaces.write(MozartSpaces.PARTS, p);
			} catch (Exception e) {
				logger.error("Write propellant exception", e);
			}
		}

		for (int i = 1; i <= this.woodenstaff; i++) {

			WoodenStaff w = new WoodenStaff(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));

			try {
				mozartSpaces.write(MozartSpaces.PARTS, w);
			} catch (Exception e) {
				logger.error("Write woodenstaff exception", e);
			}
		}

		for (int i = 1; i <= this.effectLoad; i++) {

			EffectLoad el = new EffectLoad(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));

			if (this.errorRate != null)
				el.setIsDefect(this.isDefectRandom(this.errorRate));
			else
				el.setIsDefect(this.isDefectRandom(10));

			try {
				mozartSpaces.write(MozartSpaces.PARTS, el);
			} catch (Exception e) {
				logger.error("Write effectload exception", e);
			}
		}

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

		try {
			ec.redirect(ec.getRequestContextPath() + "/ui/parts.xhtml");
		} catch (IOException e) {
			logger.error("", e);
		}

	}
}
