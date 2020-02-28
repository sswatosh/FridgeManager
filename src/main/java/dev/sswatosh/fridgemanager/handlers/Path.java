package dev.sswatosh.fridgemanager.handlers;

public enum Path {
    FRIDGES("fridges", "fridgeId"),
    ITEMS("items", "itemId");

    public final String collection;
    public final String id;
    public final String idPrefix;

    Path(String collection, String id) {
        this.collection = collection;
        this.id = id;
        this.idPrefix = ":" + id;
    }
}
