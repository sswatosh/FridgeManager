package dev.sswatosh.fridgemanager.domain;

public class Fridge {

    private long id;
    private final String name;

    public Fridge(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
