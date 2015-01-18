package sbc.space;

import java.lang.Thread;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

import sbc.space.SpaceTech.TransactionEndType;

public class AlterSpaceClientThread extends Thread {
    private Socket csock;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private AlterSpaceServer space;
    private ArrayList<SpaceTransaction> trans_map;

    AlterSpaceClientThread(Socket client, AlterSpaceServer as) {
	space = as;
	try {
		csock = client;
		ois = new ObjectInputStream(csock.getInputStream());
		oos = new ObjectOutputStream(new BufferedOutputStream(csock.getOutputStream()));
		trans_map = new ArrayList<SpaceTransaction>();
	} catch (Exception e) {
		System.err.println ("AlterSpaceClient: Exception in client init: " + e.getMessage());
	}
    }

    public void run() {
	  while (true) {
	   AlterMessage am;
	   try {
		am = (AlterMessage) ois.readObject();
		switch (am.getType()) {
		    case GET_CONTAINER:
//			 System.out.println ("Looking for Container " + am.getId());
//			 System.out.println ("Result for Container " + space.findContainer(am.getId()));
			 oos.writeObject(space.findContainer(am.getId()));
			 break;
		    case SET_CONTAINER:
			 oos.writeObject(space.createContainer(am.getId(), 4096));
			 break;
		    case ACT_TAKE:
			 AlterMessageTake amt = (AlterMessageTake) am;
			 ArrayList<SpaceEntry> ol = space.take(amt.getContainer(), amt.getTransaction(),
				amt.getSelector(), amt.getCount(), amt.getQuery());
			 AlterMessageGet amg = new AlterMessageGet(ol.size());
			 oos.writeObject(amg);
			 for (int i=0;i<ol.size();i++) {
				oos.writeObject(ol.get(i));
			 }
			 break;
		    case START_TRANSACTION:
		     SpaceTransaction at = space.createTransaction();
		     trans_map.add(at);
			 oos.writeObject(at);
			 break;
		    case END_TRANSACTION:
			 AlterMessageEndTransaction amet = (AlterMessageEndTransaction) am; //ois.readObject();
			 space.endTransaction(amet.getTransaction(), amet.getTET());
			 trans_map.remove(amet.getTransaction());
//			 System.out.println ("Ended transaction...");
			 oos.writeObject(new AlterMessage(AlterMessage.AlterMessageType.AMT_RESULT, null));
			 break;
		    case ACT_WRITE:
			 AlterMessageWrite amw = (AlterMessageWrite) am;
			 SpaceEntry se = (SpaceEntry) ois.readObject();
			 space.write(amw.getContainer(), amw.getTransaction(), se);
			 oos.writeObject(new AlterMessage(AlterMessage.AlterMessageType.AMT_RESULT, null));
			 break;
		}
		oos.flush();
	   } catch (java.io.EOFException eof) {
		 break;
	   } catch (Exception e) {
		System.err.println ("Exception while reading object -> " + e.getMessage());
		e.printStackTrace();
		break;
	   }
	  }
	  System.err.println("Aborting client...");
	  try { csock.close(); } catch (Exception e) {}
	  for (int i=0;i<trans_map.size();i++) {
		  AlterSpaceTransaction at = space.mapTransaction((AlterTransaction)trans_map.get(i));
		  if (at != null)
			  at.endTransaction(TransactionEndType.TET_ROLLBACK);
	  }
    }
}
