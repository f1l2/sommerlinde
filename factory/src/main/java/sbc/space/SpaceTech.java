package sbc.space;

import java.util.ArrayList;

public abstract class SpaceTech {
	protected long timeout = 100000;

	public enum SelectorType {
		SEL_ANY, SEL_FIFO, SEL_LIFO, SEL_LINDA
	}

	public enum TransactionEndType {
		TET_COMMIT, TET_ROLLBACK, TET_ABORT
	}

	public void init() {
		System.out.println("*** Initiating SpaceTech");
	}

	public abstract void exit();

	public abstract Container createContainer(String id, int size) throws Exception;

	public abstract Container findContainer(String id) throws Exception;

	public abstract SpaceTransaction createTransaction() throws Exception;

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

	public void setTimeout(long to) {
		timeout = to;
	}
}
