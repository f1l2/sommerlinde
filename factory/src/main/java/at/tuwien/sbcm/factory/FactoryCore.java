package at.tuwien.sbcm.factory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.LifoCoordinator;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants.Container;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FactoryCore {

	public static Capi CAPI;

	public static final URI SPACE_URI = URI.create("xvsm://localhost:9876");
	private static MzsCore CORE;

	public static final String PARTS = "parts";
	private static ContainerReference partsCRef;

	public static final String PRODUCED_ROCKETS = "producedRockets";
	private static ContainerReference producedRocketsCRef;

	public static final String GOOD_ROCKETS = "goodRockets";
	private static ContainerReference goodRocketsCRef;

	public static final String DEFECT_ROCKETS = "defectRockets";
	private static ContainerReference defetRocketsCRef;

	public static final String ROCKET_PACKAGES = "rocketPackages";
	private static ContainerReference rocketPackagesCRef;

	public static final String PRODUCER_COUNTER = "producerCounter";
	private static ContainerReference producerCounterCRef;

	public static final String PART_COUNTER = "partCounter";
	private static ContainerReference partCounterCRef;

	public static final String ROCKET_COUNTER = "rocketCounter";
	private static ContainerReference rocketCounterCRef;

	private static final Logger logger = LoggerFactory.getLogger(FactoryCore.class);

	public static void initSpace(Boolean withSpace) {

		try {

			if (withSpace) {
				// Create an embedded space
				CORE = DefaultMzsCore.newInstance();
				CAPI = new Capi(CORE);
			} else {
				CORE = DefaultMzsCore.newInstanceWithoutSpace();
				CAPI = new Capi(CORE);
			}

			// Create container
			producedRocketsCRef = FactoryCore.getOrCreateNamedContainer(PRODUCED_ROCKETS);
			goodRocketsCRef = FactoryCore.getOrCreateNamedContainer(GOOD_ROCKETS);
			defetRocketsCRef = FactoryCore.getOrCreateNamedContainer(DEFECT_ROCKETS);
			rocketPackagesCRef = FactoryCore.getOrCreateNamedContainer(ROCKET_PACKAGES);

			producerCounterCRef = FactoryCore.getOrCreateNamedContainer(PRODUCER_COUNTER);
			partCounterCRef = FactoryCore.getOrCreateNamedContainer(PART_COUNTER);
			rocketCounterCRef = FactoryCore.getOrCreateNamedContainer(ROCKET_COUNTER);

		} catch (MzsCoreException e) {
			logger.error("Error creating space.", e);
		}

	}

	public static void stopSpace() {
		CORE.shutdown(Boolean.TRUE);
	}

	public static ContainerReference getOrCreateNamedContainer(final String containerName) throws MzsCoreException {
		return getOrCreateNamedContainer(containerName, FactoryCore.CAPI);
	}

	public static ContainerReference getOrCreateNamedContainer(final String containerName, final Capi capi) throws MzsCoreException {

		ContainerReference containerReference;
		try {
			containerReference = capi.lookupContainer(containerName, SPACE_URI, RequestTimeout.DEFAULT, null);

		} catch (MzsCoreException e) {

			// For counter use LIFO Coordinator

			if (containerName.equals(PRODUCER_COUNTER) || containerName.equals(PART_COUNTER) || containerName.equals(ROCKET_COUNTER)) {
				ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
				obligatoryCoords.add(new LifoCoordinator());
				containerReference = capi.createContainer(containerName, SPACE_URI, Container.UNBOUNDED, obligatoryCoords, null, null);
				Entry entry = new Entry(1);
				capi.write(containerReference, entry);

				logger.info("Container created (LIFO coordinator): " + containerName);

			} else {

				ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
				obligatoryCoords.add(new FifoCoordinator());
				obligatoryCoords.add(new LindaCoordinator());
				containerReference = capi.createContainer(containerName, SPACE_URI, Container.UNBOUNDED, obligatoryCoords, null, null);

				logger.info("Container created: " + containerName);
			}

		}
		return containerReference;
	}

	public static void write(final String containerName, Entry entry) throws MzsCoreException {
		CAPI.write(entry, FactoryCore.getOrCreateNamedContainer(containerName));
	}

	public static void write(final String containerName, List<Entry> entry) throws MzsCoreException {
		CAPI.write(entry, FactoryCore.getOrCreateNamedContainer(containerName));
	}

	public static void read(final String containerName) throws MzsCoreException {

	}

	/*
	 * Get ID of container and increment it.
	 */

	public static Integer getIDAndIncr(final String containerName) {

		ArrayList<Integer> readEntries = new ArrayList<Integer>();
		try {
			TransactionReference tReference = CAPI.createTransaction(2000, SPACE_URI);

			ContainerReference cReference = getOrCreateNamedContainer(containerName);

			CAPI.lockContainer(cReference, tReference);

			readEntries = CAPI.take(cReference, LifoCoordinator.newSelector(), 0, tReference);

			CAPI.write(cReference, 20000, tReference, new Entry(readEntries.get(0) + 1));
			CAPI.commitTransaction(tReference);

		} catch (MzsCoreException e) {
			logger.error("", e);
			return -1;
		}

		return readEntries.get(0);

	}

	public static int workRandomTime() {

		int min = 1000, max = 3000;
		Random random = new Random();

		return (random.nextInt(max - min) + min);
	}

	public static boolean isDefectRandom(int probability) {

		Random random = new Random();
		int number = random.nextInt(100);

		if (number < probability)
			return true;
		else
			return false;
	}

	public static int workRandomValue() {

		Random random = new Random();
		return random.nextInt(40);
	}

}
