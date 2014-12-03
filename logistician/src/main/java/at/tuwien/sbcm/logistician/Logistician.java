package at.tuwien.sbcm.logistician;

import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
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
	}

	public Logistician() {

		FactoryCore.initSpace(Boolean.FALSE);

		// first receive producer id
		this.employeeId = FactoryCore.getIDAndIncr(FactoryCore.PRODUCER_COUNTER);

		this.pack();
	}

	public void pack() {

		TransactionReference tReference;
		try {

			logger.info("[" + this.getClass().getSimpleName() + "]" + " Waiting for rockets.");

			tReference = FactoryCore.CAPI.createTransaction(RequestTimeout.INFINITE, FactoryCore.SPACE_URI);

			ContainerReference cReference = FactoryCore.getOrCreateNamedContainer(FactoryCore.ROCKET);

			ArrayList<Rocket> result = FactoryCore.CAPI.take(cReference, FifoCoordinator.newSelector(2), RequestTimeout.INFINITE,
					tReference);

			logger.info("[" + this.getClass().getSimpleName() + "]" + " Took 5 rockets.");

			List<Entry> rocketPackage = new ArrayList<Entry>();
			for (Rocket rocket : result) {
				rocket.setReadyForPickUP(Boolean.TRUE);
				rocketPackage.add(new Entry(rocket));
			}

			FactoryCore.write(FactoryCore.ROCKET, rocketPackage);

			logger.info("[" + this.getClass().getSimpleName() + "]" + " Packaged 5 rockets.");

		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
