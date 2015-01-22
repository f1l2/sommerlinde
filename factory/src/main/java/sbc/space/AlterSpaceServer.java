package sbc.space;

import java.net.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import sbcm.factory.model.Rocket;

public class AlterSpaceServer extends AlterSpace {
    private ServerSocket ssock;
    private int port = 1234;
    private HashMap<String,AlterContainer> containers;
    private HashMap<String,AlterSpaceContainer> container_map;
    private HashMap<Integer,AlterSpaceTransaction> transaction_map;
    private int trans_count;
 
    public void init() {
		System.err.println ("*** Initialising AlterSpaceServer");
		super.init();
    }

    public AlterSpaceServer(int bind_port) {
		final AlterSpaceServer self = this;
		final int xport = (bind_port == 0) ? 1234 : bind_port;
		(new Thread() {
		    public void run() {
			try {
			  ssock = new ServerSocket (xport);
			  while (true) {
				Socket client = ssock.accept();
				AlterSpaceClientThread cc = new AlterSpaceClientThread(client, self);
				cc.start();
			  }
		       	} catch (Exception e) {
				System.err.println ("Exception while accepting client: " + e.getMessage());
			}
		    }
		}).start();
		containers = new HashMap<String,AlterContainer>();
		container_map = new HashMap<String,AlterSpaceContainer>();
		transaction_map = new HashMap<Integer,AlterSpaceTransaction>();
    }

    public void exit() { }

    public synchronized Container createContainer(String id, int size) {
		AlterContainer a = new AlterContainer(id, size);
		AlterSpaceContainer asc = new AlterSpaceContainer(id,size);
		containers.put(id, a);
		container_map.put(id, asc);
		return (Container) a;
    }

    public Container findContainer(String id) {
		return (Container) containers.get(id);
    }

    public AlterSpaceTransaction mapTransaction(AlterTransaction  at) {
    	return transaction_map.get(at);
    }
    
    public synchronized SpaceTransaction createTransaction(long timeout) {
		AlterSpaceTransaction ast = new AlterSpaceTransaction();
	/*	SpaceTransaction res = new SpaceTransaction();*/
		AlterTransaction at = new AlterTransaction(++trans_count);
		transaction_map.put(at.getId(),ast);
		return (SpaceTransaction)at;
	/*	return (SpaceTransaction) new AlterTransaction();*/
    }

    public void endTransaction (SpaceTransaction st, TransactionEndType tet) throws Exception {
		AlterSpaceTransaction ast = getTransaction(st); //transaction_map.get(((AlterTransaction)st).getId());
		if (ast == null)
			throw new Exception ("Null Pointer in Transaction");
		/*XXX*/
		ast.endTransaction(tet);
		synchronized(transaction_map) {
			transaction_map.remove(ast);
		}
    }

    protected AlterSpaceContainer getContainer(Container t) {
		return (t == null ? null : container_map.get(((AlterContainer)t).getId()));
    }
    protected AlterSpaceTransaction getTransaction(SpaceTransaction t) {
		return (t == null ? null : transaction_map.get(((AlterTransaction)t).getId()));
    }

    public <T extends SpaceEntry> ArrayList<T> take(Container c, SpaceTransaction t,
    		SelectorType s, int count, AlterQuery query) throws Exception {
		AlterSpaceContainer asc = getContainer(c); //container_map.get(((AlterContainer)c).getId());
		if (asc == null) throw new Exception ("Invalid Container");
	/*	if (t != null) {
			AlterSpaceTransaction ast = transaction_map.get(t);
			if (ast == null) throw new Exception ("Invalid Transaction");
			return ast.take(asc, s, count);
		}*/
		return asc.take(s, getTransaction(t), count, false, query);
	/*	return new ArrayList<T>();*/
    }
    public <T extends SpaceEntry> ArrayList<T> take(Container c, SpaceTransaction t,
    		SelectorType s, int count) throws Exception {
    	return take(c,t,s,count,null);
    }

    public <T extends SpaceEntry> void write(Container c, SpaceTransaction t,
		T entry) throws Exception {
		AlterSpaceContainer asc = getContainer(c); //container_map.get(((AlterContainer)c).getId());
		if (asc == null) throw new Exception ("Invalid Container");
	/*	if (t != null) {
			AlterSpaceTransaction ast = transaction_map.get(t);
			if (ast == null) throw new Exception ("Invalid Transaction"); 
			ast.write(asc, entry);
			return;
		}*/
		asc.write(getTransaction(t), entry);
    }

	public <T extends SpaceEntry> ArrayList<T> read(Container mc, SpaceTransaction t, AlterQuery query) throws Exception {
		ArrayList<T> res = take(mc, t, SpaceTech.SelectorType.SEL_ANY, 0, query);
		for (T entry : res)
			write(mc, t, entry);
		return res;
	}
}
