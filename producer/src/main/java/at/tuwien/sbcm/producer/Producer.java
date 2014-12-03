package at.tuwien.sbcm.producer;

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

import at.tuwien.sbcm.factory.FactoryCore;
import at.tuwien.sbcm.factory.model.EffectiveLoad;
import at.tuwien.sbcm.factory.model.Igniter;
import at.tuwien.sbcm.factory.model.Propellant;
import at.tuwien.sbcm.factory.model.Rocket;
import at.tuwien.sbcm.factory.model.WoodenStaff;

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

				logger.info("Retrieving necessary parts ...");

				tReference = FactoryCore.CAPI.createTransaction(RequestTimeout.INFINITE, FactoryCore.SPACE_URI);
				ContainerReference cReference = FactoryCore.getOrCreateNamedContainer(FactoryCore.PARTS);

				ArrayList<Rocket> resultIgniter = FactoryCore.CAPI.take(cReference, igniterSelector, TransactionTimeout.INFINITE,
						tReference);

				ArrayList<Rocket> resultPropellant = FactoryCore.CAPI.take(cReference, propellantSelector, TransactionTimeout.INFINITE,
						tReference);

				ArrayList<Rocket> resultWoodenStaff = FactoryCore.CAPI.take(cReference, woodenStaffSelector, TransactionTimeout.INFINITE,
						tReference);

				ArrayList<Rocket> resultEffectiveLoad = FactoryCore.CAPI.take(cReference, effectiveLoadSelector,
						TransactionTimeout.INFINITE, tReference);

				logger.info("Produce rocket.");

				Rocket rocket = new Rocket(null, null, false);
				rocket.setId(FactoryCore.getIDAndIncr(FactoryCore.ROCKET_COUNTER));
				rocket.setProducerId(this.producerId);

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

			}
		} while (true);
	}

}
