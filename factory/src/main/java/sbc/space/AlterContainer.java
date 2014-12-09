package sbc.space;

public class AlterContainer extends Container {
    private String id;
    private boolean in_transaction;
    AlterContainer(String name, int size) {
	id = name;
    }
    public String getId() { return id; }
}
