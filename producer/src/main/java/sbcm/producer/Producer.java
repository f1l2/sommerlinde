package sbcm.producer;

import java.util.ArrayList;

import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.LindaCoordinator.LindaSelector;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbcm.factory.FactoryCore;
import sbcm.factory.model.EffectiveLoad;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.Rocket;
import sbcm.factory.model.WoodenStaff;

public class Producer {

	private static final Logger logger = LoggerFactory.getLogger(Producer.class);

	private int producerId;

	public static void main(String[] args) {

		new Producer();

		System.exit(0);
	}

	public Producer() {

		FactoryCore.initSpace(Boolean.FALSE);

		// first receive producer id
		this.producerId = FactoryCore.getIDAndIncr(FactoryCore.PRODUCER_COUNTER);

		logger.info(this.producerId + " started.");

		this.produce();
	}

	public void produce() {

		TransactionReference tReference = null;

		do {

			try {

				LindaSelector igniterSelector = LindaCoordinator.newSelector(new Igniter(), 1);
				LindaSelector propellantSelector = LindaCoordinator.newSelector(new Propellant(), 1);
				LindaSelector woodenStaffSelector = LindaCoordinator.newSelector(new WoodenStaff(), 1);
				LindaSelector effectiveLoadSelector = LindaCoordinator.newSelector(new EffectiveLoad(), 3);

				tReference = FactoryCore.CAPI.createTransaction(RequestTimeout.INFINITE, FactoryCore.SPACE_URI);
				ContainerReference cReference = FactoryCore.getOrCreateNamedContainer(FactoryCore.PARTS);

				ArrayList<Igniter> resultIgniter = FactoryCore.CAPI.take(cReference, igniterSelector, TransactionTimeout.INFINITE,
						tReference);

				ArrayList<WoodenStaff> resultWoodenStaff = FactoryCore.CAPI.take(cReference, woodenStaffSelector,
						TransactionTimeout.INFINITE, tReference);

				ArrayList<EffectiveLoad> resultEffectiveLoad = FactoryCore.CAPI.take(cReference, effectiveLoadSelector,
						TransactionTimeout.INFINITE, tReference);

				int propellantAmount = 110 + FactoryCore.workRandomValue();
				Boolean isPropellantReached = false;

				ArrayList<Propellant> resultPropellant = new ArrayList<Propellant>();

				while (!isPropellantReached) {

					ArrayList<Propellant> tempResultPropellant = FactoryCore.CAPI.take(cReference, propellantSelector,
							TransactionTimeout.INFINITE, tReference);

					Propellant propellant = tempResultPropellant.get(0);

					resultPropellant.add(propellant);

					if (propellant.getAmount() >= propellantAmount) {

						propellant.setAmount(propellant.getAmount() - propellantAmount);

						if (propellant.getAmount() > 0) {
							FactoryCore.write(FactoryCore.PARTS, new Entry(propellant));
						}

						isPropellantReached = true;

					} else {
						propellantAmount = propellantAmount - propellant.getAmount();
					}
				}

				logger.info("Produce rocket.");
				Thread.sleep(FactoryCore.workRandomTime());

				Rocket rocket = new Rocket(null, null, false);
				rocket.setId(FactoryCore.getIDAndIncr(FactoryCore.ROCKET_COUNTER));
				rocket.setProducerId(this.producerId);
				rocket.setIgniter(resultIgniter.get(0));
				rocket.setWoodenStaff(resultWoodenStaff.get(0));
				rocket.setPropellant(resultPropellant);
				rocket.setEffectiveLoad(resultEffectiveLoad);

				FactoryCore.write(FactoryCore.PRODUCED_ROCKETS, new Entry(rocket));

				logger.info(this.producerId + " produced a rock (ID = " + rocket.getId() + ").");

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
