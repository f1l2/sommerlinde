package sbc.space;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AlterQuery implements Serializable {

	private int sortstyle;
	private String sortprop;
	private int count;
	ArrayList<String> properties;
	private String property;
	ArrayList<Object> propvals;
	private Object propval;
	private String cname;
	private boolean sorting;
	
	public int getCount() { return count; }
	public AlterQuery sortup(String propname) {
		property = propname;
		sorting = true;
		return this;
	}
	public AlterQuery getClass(Object o) {
		cname = o.getClass().getName();
		return this;
	}
	public AlterQuery cnt(int cnt) {
		if (cnt == -2) cnt = -1;
		count = cnt;
		return this;
	}
/*	public AlterQuery filterProperty(String property, GenericF value) {
		propval = value;
		return this;
	}*/
	public AlterQuery prop(String propname) {
		if (property != null) {
			if (properties == null) {
				properties = new ArrayList<String>();
				properties.add(property);
			}
			properties.add(propname);
		} else
			property = propname;
		return this;
	}
	public AlterQuery equaling(Object t) {
		if (propval != null) {
			if (propvals == null) {
				propvals = new ArrayList<Object>();
				propvals.add(propval);
			}
			propvals.add(t);
		} else
			propval = t;
		return this;
	}
	
	public <T extends SpaceEntry> ArrayList<T> execL(ArrayList<T> list) {
		ArrayList<T> res = new ArrayList<T>();
		HashMap<Integer, T> map = new HashMap<Integer,T>();
		int cnt = 0;

		try {
			int ref = 0;
			int idx = 0;
			for (int i=0;i<list.size();i++) {
				if (count != -1 && cnt == count && !sorting)
					break;
				T e = list.get(i);
				if (cname != null) {
					if (e.getClass().getName().equals(cname)) {
						res.add(e);
						cnt++;
						continue;
					}
				}
				if (property != null) {
					try { 
						Method m = e.getClass().getMethod(property);
						Object o = m.invoke(e);
						
						if (propval != null && o.equals(propval)) {
							int x = 0;
							if (properties != null) {
								for (x=0;x<properties.size();x++) {
									Method m2 = e.getClass().getMethod(properties.get(x));
									Object o2 = m2.invoke(e);
									
									if (propvals.get(x) == null || !o2.equals(propvals.get(x)))
										break;
								}
								if (x == properties.size())
									x = 0;
								else x = 1;
							}
							if (x != 0)
								continue;
						}
						if (sorting)
							map.put(ref, e);
						else {
							res.add(e);
							cnt++;
						}
					} catch (Exception ex) {
						continue;
					}
				}
			}
//			System.out.println ("Finished");
			
//			if (sorting) return list.get(idx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sorting) {
			ArrayList<Integer> keys = new ArrayList<Integer>(map.keySet());
			Collections.sort(keys);
			Iterator<Integer> iter = keys.iterator();
			while (iter.hasNext()) {
				if (count != -1 && cnt == count) break;
				cnt++;
				res.add(map.get(iter.next()));
			}
		}
		return res;
	}
	
	public <T extends SpaceEntry> T exec(ArrayList<T> list) {
//		System.out.println ("AlterQuery::exec");

		try {
			int ref = 0;
			int idx = 0;
			for (int i=0;i<list.size();i++) {
				T e = list.get(i);
				if (cname != null) {
					if (e.getClass().getName().equals(cname))
						return e;
				}
//				System.err.println ("Searching for " + e);
				if (property != null) {
					try { 
					Method m = e.getClass().getMethod(property);
					Object o = m.invoke(e);
					
					if (propval != null && o.equals(propval)) {
						return e;
					}
				
					if (sorting) {
						if ((Integer)o > ref) {
							ref = (Integer) o;
							idx = i;
						}
					}
					} catch (Exception ex) {
						continue;
					}

				}
			}
//			System.out.println ("Finished");
			if (sorting) return list.get(idx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
