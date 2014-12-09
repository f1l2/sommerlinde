package sbc.space;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.LifoCoordinator;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.TransactionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbcm.factory.Factory;
import sbcm.factory.model.Employee;

public class MozartSpaces extends SpaceTech {
	private URI spaceURI;
	private MzsCore core;
	private Capi capi;

	private static final Logger logger = LoggerFactory.getLogger(Factory.class);

	// geändert Manuel
	public static final String PARTS = "parts";
	public static final String PRODUCED_ROCKETS = "producedRockets";
	public static final String GOOD_ROCKETS = "goodRockets";
	public static final String DEFECT_ROCKETS = "defectRockets";
	public static final String ROCKET_PACKAGES = "rocketPackages";
	public static final String PRODUCER_COUNTER = "producerCounter";
	public static final String PART_COUNTER = "partCounter";
	public static final String ROCKET_COUNTER = "rocketCounter";
	public static final String PACKAGE_COUNTER = "packageCounter";

	public MozartSpaces(boolean newspace) {
		try {
			spaceURI = new URI("xvsm://localhost:9876/");
			if (!newspace)
				core = DefaultMzsCore.newInstanceWithoutSpace();
			else
				core = DefaultMzsCore.newInstance();
			capi = new Capi(core);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exit() {
		try {
			core.shutdown(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		logger.info("*** Initialising MozartSpaces");

		// geändert Manuel
		try {
			this.createContainer(PRODUCED_ROCKETS, 1000);
			this.createContainer(GOOD_ROCKETS, 1000);
			this.createContainer(DEFECT_ROCKETS, 1000);
			this.createContainer(ROCKET_PACKAGES, 1000);
			this.createContainer(PARTS, 1000);
			this.createContainer(PRODUCER_COUNTER, 1000);
			this.createContainer(PART_COUNTER, 1000);
			this.createContainer(ROCKET_COUNTER, 1000);
			this.createContainer(PACKAGE_COUNTER, 1000);

			// write inital value
			MozartTransaction mt = (MozartTransaction) this.createTransaction();
			this.write(this.findContainer(PART_COUNTER), mt, new Employee(1));
			this.write(this.findContainer(PRODUCER_COUNTER), mt, new Employee(1));
			this.write(this.findContainer(ROCKET_COUNTER), mt, new Employee(1));
			this.write(this.findContainer(PACKAGE_COUNTER), mt, new Employee(1));

			this.endTransaction(mt, TransactionEndType.TET_COMMIT);

		} catch (Exception e) {
			logger.error("Error initalising MozartSpaces", e);
		}
	}

	public void setCoordinator(int coordinator) {
	}

	public Container createContainer(String id, int size) throws Exception {
		return (Container) new MozartContainer(capi.createContainer(id, spaceURI, size, Arrays.asList(new FifoCoordinator(),
				new LifoCoordinator(), new AnyCoordinator(), new LindaCoordinator(), new QueryCoordinator()), null, null));
	}

	public Container findContainer(String id) throws Exception {
		return (Container) new MozartContainer(capi.lookupContainer(id, spaceURI, timeout, null));
	}

	public <T extends SpaceEntry> ArrayList<T> take(Container c, SpaceTransaction t, SelectorType selector, int count) throws Exception {
		return this.take((MozartContainer) c, (MozartTransaction) t, selector, count);
	}

	public <T extends SpaceEntry> void write(Container c, SpaceTransaction t, T entry) throws Exception {
		this.write((MozartContainer) c, (MozartTransaction) t, entry);
	}

	public <T extends SpaceEntry> void write(String containerId, T entry) throws Exception {
		capi.write(((MozartContainer) this.findContainer(containerId)).getContainer(), new Entry(entry));
	}

	public <T extends SpaceEntry> ArrayList<T> take(MozartContainer mc, MozartTransaction mt, SelectorType selector, int count)
			throws Exception {
		ArrayList<T> entries = capi.take(mc.getContainer(), _gS(selector, count), timeout, null);
		return entries;
	}

	public <T extends SpaceEntry> void write(MozartContainer mc, MozartTransaction mt, T entry) throws Exception {
		capi.write(new Entry(entry), mc.getContainer(), timeout, null);
	}

	// Manuel geändert
	public <T extends SpaceEntry> ArrayList<T> read(MozartContainer mc, MozartTransaction mt, MozartSelector selector) throws Exception {
		ArrayList<T> entries = capi.read(mc.getContainer(), selector.getSelector(), timeout, null);

		return entries;
	}

	// Manuel geändert
	public <T extends SpaceEntry> ArrayList<T> take(MozartContainer mc, MozartTransaction mt, MozartSelector selector) throws Exception {
		ArrayList<T> entries = capi.take(mc.getContainer(), selector.getSelector(), timeout, null);

		return entries;
	}

	public SpaceTransaction createTransaction() throws Exception {
		return (SpaceTransaction) new MozartTransaction(capi.createTransaction(timeout, spaceURI));
	}

	public SpaceTransaction createTransaction(long timeout) throws Exception {
		return (SpaceTransaction) new MozartTransaction(capi.createTransaction(timeout, spaceURI));
	}

	public void endTransaction(SpaceTransaction st, TransactionEndType tet) throws Exception {
		endTransaction((MozartTransaction) st, tet);
	}

	public void endTransaction(MozartTransaction mt, TransactionEndType tet) throws Exception {
		switch (tet) {
		case TET_COMMIT:
			capi.commitTransaction(_gT(mt));
			break;
		case TET_ROLLBACK:
			capi.rollbackTransaction(_gT(mt));
			break;
		}
	}

	public int getIDAndIncr(String counter) {
		// geändert Manuel

		ArrayList<SpaceEntry> entries = new ArrayList<SpaceEntry>();
		int id = -1;
		try {

			MozartTransaction mt = (MozartTransaction) this.createTransaction();

			MozartContainer mc = (MozartContainer) this.findContainer(counter);

			entries = this.take(mc, mt, SelectorType.SEL_LIFO, 1);

			if (null != entries) {

				id = entries.get(0).getId();
				this.write(mc, mt, new Employee(entries.get(0).getId() + 1));
			}

			this.endTransaction(mt, TransactionEndType.TET_COMMIT);

		} catch (Exception e) {
			logger.error("", e);
		}

		return id;

	}

	private TransactionReference _gT(MozartTransaction mt) {
		if (mt != null)
			return mt.getTransaction();
		return null;
	}

	private ContainerReference _gC(MozartContainer mc) {
		if (mc != null)
			return mc.getContainer();
		return null;
	}

	private org.mozartspaces.capi3.Selector _gS(SelectorType sel, int count) {
		switch (sel) {
		case SEL_FIFO:
			return FifoCoordinator.newSelector(count);
		case SEL_LIFO:
			return LifoCoordinator.newSelector(count);
		}
		return AnyCoordinator.newSelector(count);
	}
}