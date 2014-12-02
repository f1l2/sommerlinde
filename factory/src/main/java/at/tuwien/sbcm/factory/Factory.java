package at.tuwien.sbcm.factory;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;

public class Factory {

	final static Logger logger = Logger.getLogger(Factory.class);

	public static void main(String[] args) {

		logger.debug("Start space ...");

		SpaceCore.initSpace();

		logger.debug("Space started.");

		try {

			// create a container
			// ContainerReference container = SpaceCore.CAPI.createContainer();

			ContainerReference container = SpaceCore.getOrCreateNamedContainer("hallo", SpaceCore.CAPI);

			SpaceCore.CAPI.write(container, new Entry("Hallo"));
			SpaceCore.CAPI.write(container, new Entry("Bye"));

			TransactionReference tr = SpaceCore.CAPI.createTransaction(20000, SpaceCore.SPACE_URI);

			ArrayList<String> resultEntries = SpaceCore.CAPI.read(container, FifoCoordinator.newSelector(), 0, tr);

			for (String entry : resultEntries) {
				System.out.println("Entry read: " + entry);
			}

		} catch (MzsCoreException e) {
			logger.error("", e);
		}

		SpaceCore.stopSpace();

		logger.debug("Space stopped.");

	}
}
