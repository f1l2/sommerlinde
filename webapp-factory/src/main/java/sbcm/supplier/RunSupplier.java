package sbcm.supplier;

import javax.faces.bean.ViewScoped;
import org.slf4j.Logger;

import sbc.space.MozartSpaces;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.EffectLoadColor;
import sbcm.factory.model.Employee;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.WoodenStaff;
import sbcm.space.role.Role;

@ViewScoped
public class RunSupplier extends Role {

	private String name;
	private Integer woodenstaff = new Integer(1000);
	private Integer igniter = new Integer(1000);
	private Integer propellant = 1000;
	private Integer effectLoadRed = 1000;
	private Integer effectLoadBlue = 1000;
	private Integer effectLoadGreen = 1000;
	private Integer errorRate = 1000;

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

		new RunSupplier();

		System.exit(0);
	}

	public RunSupplier() {
		super();
	}

	@Override
	protected void doAction() {
		/* per Rocket:
		   1 Holzstab
		   1 Gehaeuse
		   3 Effektladungen
		   130g Treibladung
		 */
		woodenstaff = new Integer(100);
		igniter = new Integer(100);
		propellant = new Integer(100);
		effectLoadRed = new Integer(100);
		effectLoadBlue = new Integer(100);
		effectLoadGreen = new Integer(100);
		errorRate = new Integer(10);
		deliver();
	}

	public void deliver() {

		DeliverProcess deliverProcess = new DeliverProcess();

		System.out.println ("Going to run " + igniter);
		deliverProcess.run();
		System.out.println ("Delivered everything");
	}

	class DeliverProcess implements Runnable {

		public void run() {

			try {
				Thread.sleep(workRandomTime());
			} catch (InterruptedException e) {
				logger.error("", e);
			}

			System.out.println ("Pushing " + igniter + " Igniters");
			for (int i = 1; i <= igniter; i++) {

				Igniter ig = new Igniter(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				ig.setSupplier(new Employee(employeeId));
				try {
					mozartSpaces.write(MozartSpaces.PARTS, ig);
				} catch (Exception e) {
					logger.error("Write igniter exception", e);
				}
			}

			System.out.println ("Pushing " + propellant + " Propellants");
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

			System.out.println ("Pushing " + woodenstaff + " Wooden Staffs");
			for (int i = 1; i <= woodenstaff; i++) {

				WoodenStaff w = new WoodenStaff(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				w.setSupplier(new Employee(employeeId));

				try {
					mozartSpaces.write(MozartSpaces.PARTS, w);
				} catch (Exception e) {
					logger.error("Write woodenstaff exception", e);
				}
			}

			System.out.println ("Pushing " + effectLoadRed + " effect Load (red)");
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
			
			System.out.println ("Pushing " + effectLoadBlue + " effect Load (blue)");
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
			
			System.out.println ("Pushing " + effectLoadGreen + " effect Load (green)");
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
