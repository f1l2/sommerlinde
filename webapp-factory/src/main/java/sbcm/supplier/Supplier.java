package sbcm.supplier;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;

import sbc.space.MozartSpaces;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.EffectLoadColor;
import sbcm.factory.model.Employee;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.WoodenStaff;
import sbcm.space.role.Role;

@ManagedBean(name = "supplier")
@ViewScoped
public class Supplier extends Role {

	private String name;
	private Integer woodenstaff = 0;
	private Integer igniter = 0;
	private Integer propellant = 0;
	private Integer effectLoadRed = 0;
	private Integer effectLoadBlue = 0;
	private Integer effectLoadGreen = 0;
	private Integer errorRate = 0;

	public int getProducerId() {
		return employeeId;
	}

	public void setProducerId(int producerId) {
		employeeId = producerId;
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

	public Integer getEffectLoadRed() {
		return effectLoadRed;
	}

	public void setEffectLoadRed(Integer effectLoadRed) {
		this.effectLoadRed = effectLoadRed;
	}
	
	public Integer getEffectLoadBlue() {
		return effectLoadBlue;
	}

	public void setEffectLoadBlue(Integer effectLoadBlue) {
		this.effectLoadBlue = effectLoadBlue;
	}

	public Integer getEffectLoadGreen() {
		return effectLoadGreen;
	}

	public void setEffectLoadGreen(Integer effectLoadGreen) {
		this.effectLoadGreen = effectLoadGreen;
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

		DeliverProcess deliverProcess = new DeliverProcess();

		deliverProcess.run();

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

		try {
			ec.redirect(ec.getRequestContextPath() + "/factory/parts.xhtml");
		} catch (IOException e) {
			logger.error("", e);
		}

	}

	class DeliverProcess implements Runnable {

		public void run() {

			try {
				Thread.sleep(workRandomTime());
			} catch (InterruptedException e) {
				logger.error("", e);
			}

			for (int i = 1; i <= igniter; i++) {

				Igniter ig = new Igniter(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				ig.setSupplier(new Employee(employeeId));
				try {
					mozartSpaces.write(MozartSpaces.PARTS, ig);
				} catch (Exception e) {
					logger.error("Write igniter exception", e);
				}
			}

			for (int i = 1; i <= propellant; i++) {

				Propellant p = new Propellant(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				p.setAmount(500);
				p.setSupplier(new Employee(employeeId));

				try {
					mozartSpaces.write(MozartSpaces.PARTS, p);
				} catch (Exception e) {
					logger.error("Write propellant exception", e);
				}
			}

			for (int i = 1; i <= woodenstaff; i++) {

				WoodenStaff w = new WoodenStaff(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				w.setSupplier(new Employee(employeeId));

				try {
					mozartSpaces.write(MozartSpaces.PARTS, w);
				} catch (Exception e) {
					logger.error("Write woodenstaff exception", e);
				}
			}

			for (int i = 1; i <= effectLoadRed; i++) {

				EffectLoad el = new EffectLoad(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				el.setSupplier(new Employee(employeeId));
				el.setColor(EffectLoadColor.RED);

				if (errorRate != null)
					el.setIsDefect(isDefectRandom(errorRate));
				else
					el.setIsDefect(isDefectRandom(10));

				try {
					mozartSpaces.write(MozartSpaces.PARTS, el);
				} catch (Exception e) {
					logger.error("Write effectload exception", e);
				}
			}
			
			for (int i = 1; i <= effectLoadBlue; i++) {

				EffectLoad el = new EffectLoad(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				el.setSupplier(new Employee(employeeId));
				el.setColor(EffectLoadColor.BLUE);

				if (errorRate != null)
					el.setIsDefect(isDefectRandom(errorRate));
				else
					el.setIsDefect(isDefectRandom(10));

				try {
					mozartSpaces.write(MozartSpaces.PARTS, el);
				} catch (Exception e) {
					logger.error("Write effectload exception", e);
				}
			}
			
			for (int i = 1; i <= effectLoadGreen; i++) {

				EffectLoad el = new EffectLoad(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				el.setSupplier(new Employee(employeeId));
				el.setColor(EffectLoadColor.GREEN);
				
				if (errorRate != null)
					el.setIsDefect(isDefectRandom(errorRate));
				else
					el.setIsDefect(isDefectRandom(10));

				try {
					mozartSpaces.write(MozartSpaces.PARTS, el);
				} catch (Exception e) {
					logger.error("Write effectload exception", e);
				}
			}
		}
	}
}
