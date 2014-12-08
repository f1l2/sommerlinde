package sbcm.qsupervisor;

import java.util.ArrayList;

import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.LindaCoordinator.LindaSelector;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbcm.factory.FactoryCore;
import sbcm.factory.model.EffectiveLoad;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.Rocket;

public class QSupervisor {

	private static final Logger logger = LoggerFactory.getLogger(QSupervisor.class);

	private int employeeId;

	public static void main(String[] args) {
		new QSupervisor();
	}

	public QSupervisor() {

		FactoryCore.initSpace(Boolean.FALSE);

		// first receive producer id
		this.employeeId = FactoryCore.getIDAndIncr(FactoryCore.PRODUCER_COUNTER);

		logger.info(this.employeeId + " started.");

		this.check();
	}

	public void check() {

		do {
			TransactionReference tReference = null;

			Rocket rocketTemplate = new Rocket(null, null, false);
			LindaSelector rocketSelector = LindaCoordinator.newSelector(rocketTemplate, 1);
			try {

				logger.info("Waiting for work ... ");

				tReference = FactoryCore.CAPI.createTransaction(RequestTimeout.INFINITE, FactoryCore.SPACE_URI);
				ContainerReference cReference = FactoryCore.getOrCreateNamedContainer(FactoryCore.PRODUCED_ROCKETS);

				ArrayList<Rocket> result = FactoryCore.CAPI.take(cReference, rocketSelector, RequestTimeout.INFINITE, tReference);

				logger.info("Check rocket (Id = " + result.get(0).getId() + ").");
				Thread.sleep(FactoryCore.workRandomTime());

				Rocket rocket = result.get(0);

				/**
				 * Propellant amount has to be more than 110.
				 * 
				 * Only one defect effective load is allowed.
				 */
				int amount = 0;
				for (Propellant propellant : rocket.getPropellant()) {
					amount += propellant.getAmount();
				}

				int cntDefectEffectiveLoad = 0;
				for (EffectiveLoad effectiveLoad : rocket.getEffectiveLoad()) {
					if (effectiveLoad.getIsDefect())
						cntDefectEffectiveLoad++;
				}

				// rocket.getEmployee().add(new Employee(this.employeeId));
				if ((amount <= 110) || (cntDefectEffectiveLoad >= 2))
					rocket.setIsDefect(Boolean.TRUE);
				else
					rocket.setIsDefect(Boolean.FALSE);

				if (rocket.getIsDefect()) {
					FactoryCore.write(FactoryCore.DEFECT_ROCKETS, new Entry(rocket));
				} else {
					FactoryCore.write(FactoryCore.GOOD_ROCKETS, new Entry(rocket));
				}

				logger.info("Rocket checked: (Id = " + result.get(0).getId() + "; Defect = " + rocket.getIsDefect() + ").");

				FactoryCore.CAPI.commitTransaction(tReference);

			} catch (MzsCoreException e) {

				logger.error("", e);

				try {
					FactoryCore.CAPI.rollbackTransaction(tReference);
				} catch (MzsCoreException e1) {
					logger.error("", e1);
				}
			} catch (InterruptedException e) {
				logger.error("", e);
			}

		} while (true);
	}
}
