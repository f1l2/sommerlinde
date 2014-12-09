package sbc.space;

public class AlterMessageWrite extends AlterMessage {
    private int count;
    private AlterContainer container;
    private AlterTransaction transaction;
    AlterMessageWrite(AlterContainer co, AlterTransaction t) {
	super(AlterMessage.AlterMessageType.ACT_WRITE, null);
	count = 1;
	container = co;
	transaction = t;
    }
    public Container getContainer() { return (Container) container; }
    public SpaceTransaction getTransaction() { return (SpaceTransaction) transaction; }
}
