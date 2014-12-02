package at.tuwien.sbcm.factory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.LifoCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants.Container;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;

public abstract class SpaceCore {

	public static Capi CAPI;

	public static final URI SPACE_URI = URI.create("xvsm://localhost:9876");
	private static MzsCore CORE;

	private static final String WOODENSTAFF = "woodenstaff";
	private static ContainerReference woodenstaffCRef;

	private static final String IGNITER = "igniter";
	private static ContainerReference igniterCRef;

	private static final String PROPELLANT = "propellant";
	private static ContainerReference propellantCRef;

	private static final String EFFECTIVE_LOAD = "effectiveLoad";
	private static ContainerReference effectiveLoadCRef;

	private static final String ID_COUNTER = "idCounter";
	private static ContainerReference idCounterCRef;

	private static final Logger logger = Logger.getLogger(SpaceCore.class);

	public static void initSpace() {

		try {
			// Create an embedded space
			CORE = DefaultMzsCore.newInstance();
			CAPI = new Capi(CORE);

			// Create container
			woodenstaffCRef = SpaceCore.getOrCreateNamedContainer(WOODENSTAFF, CAPI);
			igniterCRef = SpaceCore.getOrCreateNamedContainer(IGNITER, CAPI);
			propellantCRef = SpaceCore.getOrCreateNamedContainer(PROPELLANT, CAPI);
			effectiveLoadCRef = SpaceCore.getOrCreateNamedContainer(EFFECTIVE_LOAD, CAPI);
			idCounterCRef = SpaceCore.getOrCreateNamedContainer(ID_COUNTER, CAPI);

		} catch (MzsCoreException e) {
			logger.error("Error creating space.", e);
		}

	}

	public static void stopSpace() {

		CORE.shutdown(Boolean.TRUE);

	}

	public static ContainerReference getOrCreateNamedContainer(final String containerName, final Capi capi) throws MzsCoreException {

		ContainerReference containerReference;
		try {
			containerReference = capi.lookupContainer(containerName, SPACE_URI, RequestTimeout.DEFAULT, null);

		} catch (MzsCoreException e) {

			if (containerName.equals(ID_COUNTER)) {
				ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
				obligatoryCoords.add(new LifoCoordinator());
				containerReference = capi.createContainer(containerName, SPACE_URI, Container.UNBOUNDED, obligatoryCoords, null, null);
				Entry entry = new Entry(0);
				capi.write(containerReference, entry);

				logger.debug("Container created: " + containerName);

			} else {

				ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
				obligatoryCoords.add(new FifoCoordinator());
				containerReference = capi.createContainer(containerName, SPACE_URI, Container.UNBOUNDED, obligatoryCoords, null, null);

				logger.debug("Container created: " + containerName);
			}

		}
		return containerReference;
	}

	public static int workRandomTime() {

		int min = 1000, max = 3000;
		Random random = new Random();

		return (random.nextInt(max - min) + min);
	}
}
