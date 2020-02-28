package dev.sswatosh.fridgemanager.domain;

public class Item {

    private final long id;
    private final long fridgeId;
    private final ItemType type;
    private final String name;

    public Item(long id, long fridgeId, ItemType type, String name) {
        this.id = id;
        this.fridgeId = fridgeId;
        this.type = type;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public long getFridgeId() {
        return fridgeId;
    }

    public ItemType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
