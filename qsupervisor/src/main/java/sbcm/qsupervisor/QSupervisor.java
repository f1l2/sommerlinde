package sbcm.qsupervisor;

import java.util.ArrayList;

import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.LindaCoordinator.LindaSelector;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;

import sbc.space.MozartContainer;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.Employee;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.Rocket;
import sbcm.factory.model.Role;

public class QSupervisor extends Role {

	public static void main(String[] args) {
		new QSupervisor();
	}

	public QSupervisor() {
		super();
	}

	@Override
	protected void doAction() {

		do {
			MozartTransaction mt = null;

			Rocket rocketTemplate = new Rocket();
			LindaSelector rocketSelector = LindaCoordinator.newSelector(rocketTemplate, 1);
			try {

				logger.info("Waiting for work ... ");

				mt = (MozartTransaction) this.mozartSpaces.createTransaction(TransactionTimeout.INFINITE);
				MozartContainer mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PRODUCED_ROCKETS);

				ArrayList<Rocket> result = this.mozartSpaces.take(mc, mt, rocketSelector, 1);

				logger.info("Check rocket (Id = " + result.get(0).getId() + ").");
				Thread.sleep(this.workRandomTime());

				Rocket rocket = result.get(0);

				/**
				 * Propellant amount has to be more than 120.
				 * 
				 * Only one defect effective load is allowed.
				 */
				int amount = 0;
				for (Propellant propellant : rocket.getPropellant()) {
					amount += propellant.getAmount();
				}

				int cntDefectEffectiveLoad = 0;
				for (EffectLoad effectiveLoad : rocket.getEffectiveLoad()) {
					if (effectiveLoad.getIsDefect())
						cntDefectEffectiveLoad++;
				}

				// rocket.getEmployee().add(new Employee(this.employeeId));
				if ((amount < 120) || (cntDefectEffectiveLoad > 1)) {
					rocket.setIsDefect(Boolean.TRUE);
				} else {
					rocket.setIsDefect(Boolean.FALSE);
				}

				rocket.getEmployee().add(new Employee(this.employeeId));

				if (rocket.getIsDefect()) {
					this.mozartSpaces.write(MozartSpaces.DEFECT_ROCKETS, rocket);
				} else {
					this.mozartSpaces.write(MozartSpaces.GOOD_ROCKETS, rocket);
				}

				logger.info("Rocket checked: (Id = " + result.get(0).getId() + "; Defect = " + rocket.getIsDefect() + ").");

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
