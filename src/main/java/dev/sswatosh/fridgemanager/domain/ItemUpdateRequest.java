package dev.sswatosh.fridgemanager.domain;

public class ItemUpdateRequest {

    private ItemType type;
    private String name;

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
