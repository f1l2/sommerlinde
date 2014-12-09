package sbc.space;

public class AlterMessageTake extends AlterMessage {
    private SpaceTech.SelectorType selector;
    private int count;
    private AlterContainer container;
    private AlterTransaction transaction;
    AlterMessageTake(SpaceTech.SelectorType s, int c,
	AlterContainer co, AlterTransaction t) {
	super(AlterMessage.AlterMessageType.ACT_TAKE, null);
	selector = s;
	count = c;
	container = co;
	transaction = t;
    }
    public Container getContainer() { return (Container) container; }
    public SpaceTransaction getTransaction() { return (SpaceTransaction) transaction; }
    public SpaceTech.SelectorType getSelector() { return selector; }
    public int getCount() { return count; }
}
