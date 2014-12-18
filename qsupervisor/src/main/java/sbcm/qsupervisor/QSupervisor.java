package sbcm.qsupervisor;

import java.util.ArrayList;

import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;

import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.Employee;
import sbcm.factory.model.QualityCategory;
import sbcm.factory.model.Rocket;
import sbcm.space.role.Role;

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
			MozartSelector rocketSelector = new MozartSelector(LindaCoordinator.newSelector(rocketTemplate, 1));
			try {

				logger.info("Waiting for work ... ");

				mt = (MozartTransaction) this.mozartSpaces.createTransaction(TransactionTimeout.INFINITE);
				MozartContainer mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PRODUCED_ROCKETS);

				ArrayList<Rocket> result = this.mozartSpaces.take(mc, mt, rocketSelector);

				logger.info("Check rocket (Id = " + result.get(0).getId() + ").");
				Thread.sleep(this.workRandomTime());

				Rocket rocket = result.get(0);

				/**
				 * Propellant amount has to be more than 120.
				 * 
				 * Only one defect effect load is allowed.
				 */

				int cntDefectEffectiveLoad = 0;
				for (EffectLoad effectiveLoad : rocket.getEffectiveLoad()) {
					if (effectiveLoad.getIsDefect())
						cntDefectEffectiveLoad++;
				}

		
				if ((rocket.getFillingQuantity() >= 130) && (cntDefectEffectiveLoad == 0)) {
					logger.info("Quality category A.");
					rocket.setQualityCategory(QualityCategory.A);
				} else if ((rocket.getFillingQuantity() >= 120) && (cntDefectEffectiveLoad <= 1)) {
						logger.info("Quality category B.");
					rocket.setQualityCategory(QualityCategory.B);
				} else {
					logger.info("Defect.");
					rocket.setQualityCategory(QualityCategory.DEFEKT);
				}
				
				rocket.getEmployee().add(new Employee(this.employeeId));

				if (rocket.getQualityCategory().equals(QualityCategory.DEFEKT)) {
					this.mozartSpaces.write(MozartSpaces.DEFECT_ROCKETS, rocket);
				} else {
					this.mozartSpaces.write(MozartSpaces.GOOD_ROCKETS, rocket);
				}

				logger.info("Rocket checked: (Id = " + result.get(0).getId() + "; QualityCategory = " + rocket.getQualityCategory() + ").");

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
