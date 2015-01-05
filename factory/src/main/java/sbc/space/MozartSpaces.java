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

public class MozartSpaces extends SpaceTech {
	private URI spaceURI;
	private MzsCore core;
	private Capi capi;

	private static final Logger logger = LoggerFactory.getLogger(Factory.class);

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

	public MozartSpaces(boolean newspace, String uri) {
		try {
			spaceURI = new URI(uri);
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

		super.init();
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

	public <T extends SpaceEntry> ArrayList<T> read(MozartContainer mc, MozartTransaction mt, MozartSelector selector) throws Exception {
		ArrayList<T> entries = capi.read(mc.getContainer(), selector.getSelector(), timeout, null);

		return entries;
	}

	public <T extends SpaceEntry> ArrayList<T> take(MozartContainer mc, MozartTransaction mt, MozartSelector selector) throws Exception {
		ArrayList<T> entries = capi.take(mc.getContainer(), selector.getSelector(), timeout, mt.getTransaction());
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