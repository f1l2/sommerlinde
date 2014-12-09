package sbcm.producer;

import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.LindaCoordinator.LindaSelector;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;

import sbc.space.MozartContainer;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.Employee;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.Rocket;
import sbcm.factory.model.Role;
import sbcm.factory.model.WoodenStaff;

/**
 * @author Manuel
 * 
 */
public class Producer extends Role {

	public static void main(String[] args) {

		new Producer();

		System.exit(0);
	}

	public Producer() {
		super();
	}

	@Override
	protected void doAction() {

		do {

			MozartTransaction mt = null;

			try {

				LindaSelector igniterSelector = LindaCoordinator.newSelector(new Igniter(), 1);
				LindaSelector propellantSelector = LindaCoordinator.newSelector(new Propellant(), 1);
				LindaSelector woodenStaffSelector = LindaCoordinator.newSelector(new WoodenStaff(), 1);
				LindaSelector effectLoadSelector = LindaCoordinator.newSelector(new EffectLoad(), 3);

				mt = (MozartTransaction) this.mozartSpaces.createTransaction(TransactionTimeout.INFINITE);

				MozartContainer mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PARTS);

				ArrayList<Igniter> resultIgniter = this.mozartSpaces.take(mc, mt, igniterSelector, 1);

				ArrayList<WoodenStaff> resultWoodenStaff = this.mozartSpaces.take(mc, mt, woodenStaffSelector, 1);

				ArrayList<EffectLoad> resultEffectLoad = this.mozartSpaces.take(mc, mt, effectLoadSelector, 3);

				int propellantAmount = 110 + this.workRandomValue();

				Rocket rocket = new Rocket(this.mozartSpaces.getIDAndIncr(MozartSpaces.ROCKET_COUNTER), null, false);
				rocket.setFillingQuantity(propellantAmount);
				rocket.setPropellant(new ArrayList<Propellant>());

				Boolean isPropellantReached = false;
				while (!isPropellantReached) {

					Propellant propellant = (Propellant) this.mozartSpaces.take(mc, mt, propellantSelector, 3).get(0);
					rocket.getPropellant().add(propellant);

					if (propellant.getAmount() >= propellantAmount) {

						propellant.setAmount(propellant.getAmount() - propellantAmount);

						if (propellant.getAmount() > 0) {
							this.mozartSpaces.write(MozartSpaces.PARTS, propellant);
						}

						isPropellantReached = true;

					} else {
						propellantAmount = propellantAmount - propellant.getAmount();
					}
				}

				logger.info("Produce rocket.");
				Thread.sleep(this.workRandomTime());

				List<Employee> employees = new ArrayList<Employee>();
				employees.add(new Employee(this.employeeId));

				rocket.setEmployee(employees);
				rocket.setIgniter(resultIgniter.get(0));
				rocket.setWoodenStaff(resultWoodenStaff.get(0));
				rocket.setEffectiveLoad(resultEffectLoad);

				this.mozartSpaces.write(MozartSpaces.PRODUCED_ROCKETS, rocket);
				logger.info(this.employeeId + " produced a rock (ID = " + rocket.getId() + ").");

				this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

			} catch (Exception e) {

				logger.error("", e);

				try {
					this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_ROLLBACK);
				} catch (Exception e1) {
					logger.error("", e1);
				}
			}
		} while (true);
	}
}
