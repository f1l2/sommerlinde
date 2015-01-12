package sbc.space;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;

public class AlterQuery implements Serializable {

	private int sortstyle;
	private String sortprop;
	private int count;
	private String property;
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
		count = cnt;
		return this;
	}
/*	public AlterQuery filterProperty(String property, GenericF value) {
		propval = value;
		return this;
	}*/
	public AlterQuery prop(String propname) {
		property = propname;
		return this;
	}
	public AlterQuery equaling(Object t) {
		propval = t;
		return this;
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
