package sbc.space;

import java.io.Serializable;

public class AlterMessage implements Serializable {
   /**
	 * 
	 */
   private static final long serialVersionUID = 7821298011446112779L;
   public enum AlterMessageType {
	GET_CONTAINER,
	SET_CONTAINER,
	START_TRANSACTION,
	END_TRANSACTION, 
	ACT_TAKE,
	ACT_WRITE,
	ACT_META_GET,
	AMT_RESULT
   }
   AlterMessage(AlterMessageType t, String i) {
	type = t;
	id = i;
   }
   protected AlterMessageType type;
   protected String id;
   public AlterMessageType getType() { return type; }
   public String getId() { return id; }
}
