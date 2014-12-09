package sbc.space;

import java.net.*;
import java.util.HashMap;
import java.util.ArrayList;

public abstract class AlterSpace extends SpaceTech {
    private ServerSocket ssock;
    private int port = 1234;
    private HashMap<String,AlterContainer> containers;
 
    public void init() {
	System.err.println ("*** Initialising AlterSpace");
    }

    public AlterSpace() {
    }

    public void exit() { }
/*
    public Container createContainer(String id, int size) {
	AlterContainer a = new AlterContainer(id, size);
	containers.put(id, a);
	return (Container) a;
    }

    public Container findContainer(String id) {
	return (Container) containers.get(id);
    }

    public SpaceTransaction createTransaction() {
	return (SpaceTransaction) new AlterTransaction();
    }

    public void endTransaction (SpaceTransaction st, TransactionEndType tet) {
    }

    public abstract <T extends SpaceEntry> ArrayList<T> take(Container c, SpaceTransaction t,
	SelectorType s, int count) {
    }

    public abstract <T extends SpaceEntry> void write(Container c, SpaceTransaction t, T entry) {
    }*/
}
