package sbc.space;

import java.io.Serializable;

public class AlterContainer extends Container implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2021747810742806873L;
	private String id;
    private boolean in_transaction;
    AlterContainer(String name, int size) {
	id = name;
    }
    public String getId() { return id; }
}
