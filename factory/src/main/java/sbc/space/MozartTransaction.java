package sbc.space;

import org.mozartspaces.core.TransactionReference;

public class MozartTransaction extends SpaceTransaction {
	private TransactionReference transaction;

	public MozartTransaction(TransactionReference t) {
		transaction = t;
	}

	public TransactionReference getTransaction() {
		return transaction;
	}
}
