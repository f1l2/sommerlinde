package sbc.space;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AlterSpaceContainer {
    private String id;
    private ArrayList<SpaceEntry> entries;
    private HashMap<SpaceEntry,AlterSpaceTransaction> trans_map;
    private HashMap<AlterSpaceTransaction,ArrayList<SpaceEntry>> t_del_entries;
    private HashMap<AlterSpaceTransaction,Integer> count_tw;
    private int commited_entries;
    private boolean locked;
    private ReentrantLock lock;
    private Condition waitEntries;

    AlterSpaceContainer(String name, int size) {
		id = name;
		entries = new ArrayList<SpaceEntry>();
		trans_map = new HashMap<SpaceEntry,AlterSpaceTransaction>();
		t_del_entries = new HashMap<AlterSpaceTransaction,ArrayList<SpaceEntry>>();
		count_tw = new HashMap<AlterSpaceTransaction,Integer>();
		lock = new ReentrantLock();
	    waitEntries = lock.newCondition();
    }
    public synchronized <T extends SpaceEntry> void write(AlterSpaceTransaction ast, T t) {
	Lock();
	if (ast != null) {
		ast.addContainer(this);
		trans_map.put(t, ast);
		if (count_tw.get(ast) == null)
			count_tw.put(ast,0);
		count_tw.put(ast, count_tw.get(ast)+1);
//		System.out.println ("Putting entry " + t + " in transaction");
	} else commited_entries++;
	entries.add(t);
//	System.out.println ("Added entry");
	Unlock();
//	notify();
    }
    public synchronized void Lock() {
		try {
			while (locked) wait();
		} catch (Exception e) {
			System.err.println ("Locking failed!");
		}
		locked = true;
    }
    public synchronized void UnlockOnly() {
		locked = false;
		notify (); 	
    }
    public synchronized void Unlock() {
    	UnlockOnly();
    	synchronized(entries) { entries.notify(); }
    }
    protected int getSaldo(AlterSpaceTransaction ast) {
		if (count_tw.get(ast) == null) count_tw.put(ast,0);
		if (ast != null) return count_tw.get(ast) > 0 ? count_tw.get(ast) : 0;
		return 0;
    }

    protected synchronized ArrayList<SpaceEntry> getOrSet(AlterSpaceTransaction c,
	HashMap<AlterSpaceTransaction,ArrayList<SpaceEntry>> hm) {

	ArrayList<SpaceEntry> res = hm.get(c);
	if (res == null) {
		res = new ArrayList<SpaceEntry>();
		hm.put(c,res);
	}
	return res;
    }

    protected void cleanTransaction(AlterSpaceTransaction ast) {
	count_tw.remove(ast);
	t_del_entries.remove(ast);
    }

    public void commitTransaction(AlterSpaceTransaction ast) {
//	Lock();
	for (int i=0;i<entries.size();i++) {
		if (trans_map.get(entries.get(i)) == ast) {
			trans_map.remove(entries.get(i));
			commited_entries++;
		}
	}
	if (t_del_entries.get(ast) != null)
		commited_entries -= t_del_entries.get(ast).size();
	cleanTransaction(ast);
//	t_del_entries.remove(ast);
//	Unlock();
    }

    public void rollbackTransaction(AlterSpaceTransaction ast) {
//	Lock();
	System.err.println ("*** ROLLING BACK TRANSACTION ***");
	ListIterator<SpaceEntry> ls = entries.listIterator();
//	ArrayList<SpaceEntry> as = t_del_entries.get(ast);
	while (ls.hasNext()) {
		SpaceEntry se = ls.next();
		if (trans_map.get(se) == ast)
			ls.remove();
	}
	if (t_del_entries.get(ast) != null)
		entries.addAll((Collection)t_del_entries.get(ast));
	commited_entries += t_del_entries.get(ast).size();
	cleanTransaction(ast);
//	t_del_entries.remove(ast);
//	Unlock();
    }

    public <T extends SpaceEntry> void manageEntry(T x, AlterSpaceTransaction ast, ArrayList<T> res) {
		AlterSpaceTransaction a = trans_map.get(x);
		if (a == null || a == ast) {
			res.add(x);
			if (a == null) {
//				commited_entries--;
				if (ast != null)
					getOrSet(ast,t_del_entries).add(x);
			}
			if (a == ast) count_tw.put(ast, count_tw.get(ast)-1);
		}
    }
    
    public <T extends SpaceEntry> ArrayList<T> compileList(ArrayList<T> entries, AlterSpaceTransaction ast) {
    	ArrayList<T> res = new ArrayList<T>();
    	for (int i=0;i<entries.size();i++) {
    		T x = (T) entries.get(i);
    		AlterSpaceTransaction a = trans_map.get(x);
    		if (a == ast || a == null) res.add(x);
    	}
    	return res;
    }
    public <T extends SpaceEntry> ArrayList<T> take(SpaceTech.SelectorType s,
		AlterSpaceTransaction ast, int count, boolean peek, AlterQuery query) throws Exception {
	int off;
	ListIterator<T> iter;
	ArrayList<T> res = new ArrayList<T>();


	if (count < -1) throw new Exception ("take: Invalid count: " + count);
	
	synchronized(this) {
		if (count == -1) {
			count = commited_entries + getSaldo(ast);
		}
		if (peek == false) {
			while (commited_entries + getSaldo(ast) < count) {
	//			System.out.println ("Waiting for " + count + " -> " + commited_entries + getSaldo(ast));
				wait();
			}
		}
		if (count == 0) {
			System.err.println ("take: Warning zero entries requested");
			return res;
		}
	}

//	System.out.println ("Trying to lock Container for something");
	Lock();

//	System.out.println ("Locked Container for something");
	if (ast != null)
	  ast.addContainer(this);

	if (query != null) {
		int qgot = 0;
		T t = null;
		while (qgot < count) {
			ArrayList<T> qlist = (ArrayList<T>) query.execL(compileList(entries, ast)); 
			//t = (T) query.exec(compileList(entries, ast));

			for (int i=0;i<qlist.size();i++) {
				manageEntry(qlist.get(i), ast, res);
				qgot++;
			}
			if (query.getCount() == -1) // -1 = ALL
				break;
			if (qgot < count) {
				Unlock();
				synchronized(entries) { entries.wait(); }
				Lock();
			}
			/*
//		T t = (T) query.exec(entries);
			if (t == null) {
				Unlock();
//				entries.wait();
				synchronized(entries) { entries.wait(); }
				Lock();
			}// else throw new Exception ("Query returned no entry");
			else {
				manageEntry(t, ast, res);
				qgot++;
			}*/
		}
		
		off = -1;
	}
	/* if only one entry exists, we ignore the selector and return that entry */
	else if (entries.size() == 1) {
		off = 0;
		T x = (T) entries.get(0);
		manageEntry(x, ast, res);
		/*AlterSpaceTransaction a = trans_map.get(x);

		if (a == null || a == ast) {
			res.add(x);
			if (a == null) {
//				commited_entries--;
				if (ast != null)
					getOrSet(ast,t_del_entries).add(x);
			}
			if (a == ast) count_tw.put(ast, count_tw.get(ast)-1);
		} else throw new Exception ("Code completely fucked up");*/
	} else
	switch (s) {
		case SEL_ANY:
		case SEL_FIFO:
		    off = 0;
/*			off = 0;
			res.addAll((Collection)entries.subList(0, count));
			entries.removeAll((Collection)res);*/
			if (ast != null) {
				iter = (ListIterator<T>)entries.listIterator(off);
				if (!iter.hasNext()) {
					System.out.println ("Dubios: " + commited_entries + " - " + count);
				}
				while (iter.hasNext()) {
					T x = iter.next();
					manageEntry(x, ast, res);
					if (res.size() == count) break;
				}
/*					AlterSpaceTransaction a = trans_map.get(x);
					if (a != null && a != ast) continue;
					res.add(x);
					if (a == null) {
//						commited_entries--;
						if (ast != null)
							getOrSet(ast,t_del_entries).add(x);
					}
					if (a == ast) count_tw.put(ast, count_tw.get(ast)-1);
				}*/
			}
			break;
		case SEL_LIFO:
			off = entries.size()-count;
			if (ast != null) {
				iter = (ListIterator<T>)entries.listIterator(off);
				while (iter.hasPrevious()) {
					T x = iter.previous();
					manageEntry(x, ast, res);
					if (res.size() == count) break;
				}
/*					AlterSpaceTransaction a = trans_map.get(x);
					if (a != null && a != ast) continue;
					res.add(x);
					if (a == null) {
//						commited_entries--;
						if (ast != null)
							getOrSet(ast,t_del_entries).add(x);
					}
					if (a == ast) count_tw.put(ast, count_tw.get(ast)-1);
//					if (a == ast) ast.decTW();
				}*/
			}
/*			res.addAll((Collection)entries.subList(entries.size()-count, count));
			entries.removeAll((Collection)res);*/
			break;
		default:
			throw new Exception ("Unknown Selector: " + s);
	}
/*	ArrayList<T> res = new ArrayList<T>((Collection)entries.subList(off,count));*/

	if (ast == null) {
	    if (res.size() == 0)
	    	res.addAll((Collection)entries.subList(off,count)); //XXX
	    commited_entries -= count;
	}

	entries.removeAll((Collection)res);
	Unlock();
//	System.out.println ("TAKE: Returning " + res.size() + " entries");
	if (res.size() != count && query ==  null) {
		System.out.println ("MISMATCH: " + entries.size() + " vs. " + count);
		for (int i=0;i<entries.size();i++) {
			T x = (T) entries.get(i);
			System.out.println ("++ size: " + x);
		}
		iter = (ListIterator<T>)entries.listIterator(0);
		while (iter.hasNext()) {
			T x = iter.next();
			System.out.println("-- Iterator: " + x);
		}
//		System.out.println ("ERROR: 0 size!");
	}
	return res;
    }
    public synchronized <T extends SpaceEntry> ArrayList<T> take(SpaceTech.SelectorType s,
		int count) throws Exception {
	return take(s, null, count, false, null);
    }
}
