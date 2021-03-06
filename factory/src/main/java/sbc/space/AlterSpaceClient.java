package sbc.space;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;

public class AlterSpaceClient extends SpaceTech {
    private Socket sock;
    private int port = 1234;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
 
    public void init() {
	System.err.println ("*** Initialising AlterSpace");
    }

    public AlterSpaceClient() {
	try {
	    sock = new Socket ("localhost", port);
	    oos = new ObjectOutputStream(sock.getOutputStream());
	    ois = new ObjectInputStream(sock.getInputStream());
	} catch (Exception e) {
	    System.err.println ("Connection to localhost:"+port + " failed: " + e.getMessage());
	}
    }

    public void exit() { }

    protected void _sendMessage(Object msg) {
	try {
		oos.writeObject(msg);
	} catch (Exception e) {
		System.err.println ("Writing object failed");
		e.printStackTrace();
	}
    }

    protected <T> T SendMessage(AlterMessage.AlterMessageType t, String id) {
//	System.out.println ("Sending message for id " + id);
	_sendMessage(new AlterMessage(t, id));
//	System.out.println ("Receiving message...");
	T r = recvMessage();
//	System.out.println ("Received message...");
	return r;
    }

    protected <T> T recvMessage() {
	try {
		T ret = (T) ois.readObject();
//		System.out.println ("Received Message: " + ret);
		return ret;
	} catch (Exception e) {
		e.printStackTrace();
		System.err.println ("Received object failed. Returning null");
		return null;
	}
    }

    public Container createContainer(String id, int size) {
/*	_sendMessage(new AlterMessage(AlterMessage.AlterMessageType.SET_CONTAINER, null));*/
	AlterContainer ac = SendMessage(AlterMessage.AlterMessageType.SET_CONTAINER, id);
	return (Container) ac;
    }

    public Container findContainer(String id) {
/*	_sendMessage(new AlterMessage(AlterMessage.AlterMessageType.GET_CONTAINER, id));*/
	AlterContainer ac = SendMessage(AlterMessage.AlterMessageType.GET_CONTAINER, id);
//	System.out.println ("Find Container returned " + ac);
	return (Container) ac;
    }

    public SpaceTransaction createTransaction() {
	AlterTransaction at = SendMessage(AlterMessage.AlterMessageType.START_TRANSACTION, null);
	return (SpaceTransaction) at;
    }

    public void endTransaction(SpaceTransaction st, TransactionEndType tet) {
/*	AlterMessage am = SendMessage(AlterMessage.AlterMessageType.END_TRANSACTION, null);*/
	_sendMessage(new AlterMessageEndTransaction(st, tet));
//	System.out.println ("Getting message?");
	AlterMessage am = recvMessage();
//	System.out.println ("Got message");
    }

    public <T extends SpaceEntry> ArrayList<T> take(Container c, SpaceTransaction t,
	SelectorType s, int count) {
	_sendMessage(new AlterMessageTake(s, count, (AlterContainer)c, (AlterTransaction)t));
	ArrayList<T> res = new ArrayList<T>();
	AlterMessageGet amg = recvMessage();
	for (int i=0;i<amg.getCount();i++) {
	   T a = recvMessage();
	   res.add(a);
	}
	return res;
/*	_sendMessage((AlterContainer)c);
	_sendMessage((AlterTransaction)t);*/
    }

    public <T extends SpaceEntry> void write(Container c, SpaceTransaction t, T entry) {
	_sendMessage(new AlterMessageWrite((AlterContainer)c, (AlterTransaction)t));
	_sendMessage(entry);
    }
    
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
