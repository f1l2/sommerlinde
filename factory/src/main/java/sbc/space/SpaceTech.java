package sbc.space;

import java.util.ArrayList;

import org.mozartspaces.core.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbcm.factory.Factory;
import sbcm.factory.model.Employee;

public abstract class SpaceTech {
	protected long timeout = 1000000;
	protected static final Logger logger = LoggerFactory.getLogger(Factory.class);

	public static final String PARTS = "parts";
	public static final String REQUESTED_ROCKETS = "requestedRockets";
	public static final String PRODUCED_ROCKETS = "producedRockets";
	public static final String GOOD_ROCKETS_A = "goodRocketsA";
	public static final String GOOD_ROCKETS_B = "goodRocketsB";
	public static final String GOOD_ROCKETS_ORDER = "goodRocketsOrder";
	public static final String DEFECT_ROCKETS = "defectRockets";
	public static final String ROCKET_PACKAGES = "rocketPackages";
	public static final String ORDERS = "orders";
	public static final String PRODUCER_COUNTER = "producerCounter";
	public static final String PART_COUNTER = "partCounter";
	public static final String ROCKET_COUNTER = "rocketCounter";
	public static final String PACKAGE_COUNTER = "packageCounter";
	public static final String ORDER_COUNTER = "orderCounter";
	public static final String STOCK = "stock";

	public enum SelectorType {
		SEL_ANY, SEL_FIFO, SEL_LIFO, SEL_LINDA
	}

	public enum TransactionEndType {
		TET_COMMIT, TET_ROLLBACK, TET_ABORT
	}

	public void init() {
		System.out.println("*** Initiating SpaceTech");
		try {
			int container_size = 20000;
			this.createContainer(PRODUCED_ROCKETS, container_size);
			this.createContainer(REQUESTED_ROCKETS, container_size);
			this.createContainer(GOOD_ROCKETS_A, container_size);
			this.createContainer(GOOD_ROCKETS_B, container_size);
			this.createContainer(GOOD_ROCKETS_ORDER, container_size);
			this.createContainer(DEFECT_ROCKETS, container_size);
			this.createContainer(ROCKET_PACKAGES, container_size);
			this.createContainer(PARTS, container_size);
			this.createContainer(ORDERS, container_size);
			this.createContainer(PRODUCER_COUNTER, container_size);
			this.createContainer(PART_COUNTER, container_size);
			this.createContainer(ROCKET_COUNTER, container_size);
			this.createContainer(PACKAGE_COUNTER, container_size);
			this.createContainer(ORDER_COUNTER, container_size);
			this.createContainer(STOCK, container_size);

			// write initial value
			SpaceTransaction mt = createTransaction();
			write(this.findContainer(PART_COUNTER), mt, new Employee(1));
			write(this.findContainer(PRODUCER_COUNTER), mt, new Employee(1));
			write(this.findContainer(ROCKET_COUNTER), mt, new Employee(1));
			write(this.findContainer(PACKAGE_COUNTER), mt, new Employee(1));
			write(this.findContainer(ORDER_COUNTER), mt, new Employee(1));

			endTransaction(mt, TransactionEndType.TET_COMMIT);

		} catch (Exception e) {
			logger.error("Error initalising MozartSpaces", e);
		}
	}

	public abstract void exit();

	public abstract Container createContainer(String id, int size) throws Exception;

	public abstract Container findContainer(String id) throws Exception;

	public abstract SpaceTransaction createTransaction(long timeout) throws Exception;
	public SpaceTransaction createTransaction() throws Exception { return createTransaction(timeout); }

	public abstract void endTransaction(SpaceTransaction transaction, TransactionEndType tet) throws Exception;

	public abstract <T extends SpaceEntry> ArrayList<T> take(Container c, SpaceTransaction t, SelectorType selector, int count)
			throws Exception;

	public <T extends SpaceEntry> T take(Container c, SpaceTransaction t) throws Exception {
		ArrayList<T> a = take(c, t, SelectorType.SEL_FIFO, 1);
		return a.get(0);
	}

	/*
	 * public abstract <T extends SpaceEntry> void write(Container c,
	 * SpaceTransaction t, ArrayList<T> entries) throws Exception;
	 */
	public abstract <T extends SpaceEntry> void write(Container c, SpaceTransaction t, T entry) throws Exception;

	public <T extends SpaceEntry> void write(String containerId, T entry) throws Exception {
		this.write(this.findContainer(containerId), null, entry);
	}

	public void setTimeout(long to) {
		timeout = to;
	}

	public int getIDAndIncr(String counter) {
		ArrayList<SpaceEntry> entries = new ArrayList<SpaceEntry>();
		int id = -1;
		try {

//OPT			SpaceTransaction mt = createTransaction();

			Container mc = findContainer(counter);

			entries = take(mc, null, SelectorType.SEL_LIFO, 1);

			if (null != entries) {

				id = entries.get(0).getId();
				write(mc, null, new Employee(entries.get(0).getId() + 1));
			}

//OPT			endTransaction(mt, TransactionEndType.TET_COMMIT);

		} catch (Exception e) {
			logger.error("", e);
		}

		return id;

	}

}
