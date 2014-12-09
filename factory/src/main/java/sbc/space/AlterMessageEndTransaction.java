package sbc.space;

import java.io.Serializable;

public class AlterMessageEndTransaction extends AlterMessage {
   AlterMessageEndTransaction(SpaceTransaction st, SpaceTech.TransactionEndType tet) {
	super(AlterMessage.AlterMessageType.END_TRANSACTION, null);
	transaction = st;
	sttet = tet;
   }
   private SpaceTransaction transaction;
   private SpaceTech.TransactionEndType sttet;
   public SpaceTransaction getTransaction() { return transaction; }
   public SpaceTech.TransactionEndType getTET() { return sttet; }
}
