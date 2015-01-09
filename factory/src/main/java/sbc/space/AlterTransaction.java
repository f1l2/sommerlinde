package sbc.space;

import java.io.Serializable;

public class AlterTransaction extends SpaceTransaction implements Serializable  {
   /**
	 * 
	 */
   private static final long serialVersionUID = -4883967749123083429L;
   private int id;
   public int getId() { return id; }
   AlterTransaction(int i) { id = i; }
}
