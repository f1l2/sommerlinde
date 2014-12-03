package at.tuwien.sbcm.logistician;

import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.sbcm.factory.FactoryCore;
import at.tuwien.sbcm.factory.model.Rocket;

public class Logistician {

	private static final Logger logger = LoggerFactory.getLogger(Logistician.class);

	private int employeeId;

	public static void main(String[] args) {
		new Logistician();

		System.exit(0);
	}

	public Logistician() {

		FactoryCore.initSpace(Boolean.FALSE);

		// first receive producer id
		this.employeeId = FactoryCore.getIDAndIncr(FactoryCore.PRODUCER_COUNTER);

		logger.info(this.employeeId + " started.");

		this.pack();
	}

	public void pack() {

		TransactionReference tReference = null;

		do {

			try {

				tReference = FactoryCore.CAPI.createTransaction(RequestTimeout.INFINITE, FactoryCore.SPACE_URI);

				ContainerReference cReference = FactoryCore.getOrCreateNamedContainer(FactoryCore.GOOD_ROCKETS);

				ArrayList<Rocket> result = FactoryCore.CAPI.take(cReference, FifoCoordinator.newSelector(5), TransactionTimeout.INFINITE,
						tReference);

				logger.info("Package rockets ...");
				Thread.sleep(FactoryCore.workRandomTime());

				List<Entry> rocketPackage = new ArrayList<Entry>();
				for (Rocket rocket : result) {
					rocket.setReadyForPickUP(Boolean.TRUE);
					rocketPackage.add(new Entry(rocket));
				}

				FactoryCore.write(FactoryCore.ROCKET_PACKAGES, rocketPackage);

				FactoryCore.CAPI.commitTransaction(tReference);

				logger.info("Rocket package created.");

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
