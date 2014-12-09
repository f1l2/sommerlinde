package sbcm.supplier;

import org.slf4j.Logger;

import sbc.space.MozartSpaces;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.WoodenStaff;
import sbcm.space.role.Role;

public class Supplier extends Role {

	private int propDefect = 10;

	private String name;
	private Integer woodenstaff;
	private Integer igniter;
	private Integer propellant;
	private Integer effectLoad;

	public int getProducerId() {
		return employeeId;
	}

	public void setProducerId(int producerId) {
		this.employeeId = producerId;
	}

	public int getPropDefect() {
		return propDefect;
	}

	public void setPropDefect(int propDefect) {
		this.propDefect = propDefect;
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
		logger.info(this.employeeId + " started.");

		for (int i = 1; i <= 4; i++) {

			Igniter ig = new Igniter(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
			try {
				mozartSpaces.write(MozartSpaces.PARTS, ig);
			} catch (Exception e) {
				logger.error("Write igniter exception", e);
			}
		}

		for (int i = 1; i <= 4; i++) {

			Propellant p = new Propellant(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
			p.setAmount(500);

			try {
				mozartSpaces.write(MozartSpaces.PARTS, p);
			} catch (Exception e) {
				logger.error("Write propellant exception", e);
			}
		}

		for (int i = 1; i <= 4; i++) {

			WoodenStaff w = new WoodenStaff(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));

			try {
				mozartSpaces.write(MozartSpaces.PARTS, w);
			} catch (Exception e) {
				logger.error("Write woodenstaff exception", e);
			}
		}

		for (int i = 1; i <= 4; i++) {

			EffectLoad el = new EffectLoad(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
			el.setIsDefect(this.isDefectRandom(this.propDefect));

			try {
				mozartSpaces.write(MozartSpaces.PARTS, el);
			} catch (Exception e) {
				logger.error("Write effectload exception", e);
			}
		}

	}
}
