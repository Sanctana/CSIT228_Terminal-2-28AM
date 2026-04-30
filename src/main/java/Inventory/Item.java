package Inventory;

import entity.Player.Character;

public abstract class Item {
    protected String name;
    protected String description;
    private int quantity;

    public Item(String name, String description, int quantity) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    public abstract void use(Character character);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isUsable() {
        return quantity > 0;
    }

    public boolean use() {
        if (quantity > 0) {
            quantity--;
            return true;
        }
        return false;
    }
}