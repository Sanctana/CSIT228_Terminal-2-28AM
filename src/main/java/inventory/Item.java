package inventory;

import entity.player.Character;

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

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
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
