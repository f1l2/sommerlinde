package sbc.space;

import java.util.*;

public class AlterSpaceTransaction extends SpaceTransaction {
   private HashMap<AlterSpaceContainer,ArrayList<SpaceEntry>> take_entries;
   private HashMap<AlterSpaceContainer,ArrayList<SpaceEntry>> write_entries;
   private ArrayList<AlterSpaceContainer> containers;
   private int tw_saldo = 0;

   public int getSaldo() { return tw_saldo < 0 ? 0 : tw_saldo; }
   public int incTW() { return ++tw_saldo; }
   public int decTW() { return --tw_saldo; }

   AlterSpaceTransaction() {
	take_entries = new HashMap<AlterSpaceContainer,ArrayList<SpaceEntry>>();
	write_entries = new HashMap<AlterSpaceContainer,ArrayList<SpaceEntry>>();
	containers = new ArrayList<AlterSpaceContainer>();
   }

   public void addContainer(AlterSpaceContainer c) {
	if (!containers.contains(c)) containers.add(c);
   }

   public void endTransaction(SpaceTech.TransactionEndType tet) {
//	System.err.println ("Ending Transaction for " + containers.size());
	for (int i = 0; i < containers.size(); i++) {
		containers.get(i).Lock();
	}
	for (int i = 0; i < containers.size(); i++) {
		switch (tet) {
			case TET_COMMIT:
				containers.get(i).commitTransaction(this);
				break;
			case TET_ROLLBACK: case TET_ABORT:
				containers.get(i).rollbackTransaction(this);
				break;
		}
	}
	for (int i = 0; i < containers.size(); i++) {
		containers.get(i).Unlock();
	}
   }

   protected synchronized ArrayList<SpaceEntry> getOrSet(AlterSpaceContainer c,
	HashMap<AlterSpaceContainer,ArrayList<SpaceEntry>> hm) {

	ArrayList<SpaceEntry> res = hm.get(c);
	if (res == null) {
		res = new ArrayList<SpaceEntry>();
		hm.put(c,res);
	}
	return res;
   }

   public synchronized <T extends SpaceEntry> void write(AlterSpaceContainer c, T entry) {
	ArrayList<SpaceEntry> al = getOrSet(c, write_entries);
/*	if (al == null) {
		al = new ArrayList<SpaceEntry>();
		write_entries.put(c,al);
	}*/
	al.add(entry);
   }

   public <T extends SpaceEntry> ArrayList<T> take(AlterSpaceContainer c,
	SpaceTech.SelectorType s, int count) throws Exception {

/*	ArrayList<SpaceEntry> wal = getOrSet(c, write_entries);
	ArrayList<SpaceEntry> tal = getOrSet(c, take_entries);
	ArrayList<T> res;
	int cnt;

	switch (s) {
		case SEL_FIFO:
			res = c.take(s, count, true);
			if (res != null) {
			    tal.addAll((Collection)res);
			}
			cnt = res.size();
			if (cnt < count) {
			}
			break;
	}*/
	return null;
   }
/*
   protected void rollbackTransaction() {
   }

   protected void commitTransaction() {
   }*/
}
