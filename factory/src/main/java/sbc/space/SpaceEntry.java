package sbc.space;

public class SpaceEntry implements java.io.Serializable {
	private int id;
	private int type;
	private boolean damaged;
	private int quant;

	public SpaceEntry(int i, int t, boolean d, int q) {
		id = i;
		type = t;
		damaged = d;
		quant = q;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public boolean isDamaged() {
		return damaged;
	}

	public int getQuantity() {
		return quant;
	}
}
