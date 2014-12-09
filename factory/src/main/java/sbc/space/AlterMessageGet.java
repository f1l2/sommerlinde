package sbc.space;

public class AlterMessageGet extends AlterMessage {
    private int count;
    AlterMessageGet(int c) {
	super(AlterMessage.AlterMessageType.ACT_META_GET, null);
	count = c;
    }
    public int getCount() { return count; }
}
