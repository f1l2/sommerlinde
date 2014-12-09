package sbc.space;

import java.io.Serializable;

public class AlterMessage implements Serializable {
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
