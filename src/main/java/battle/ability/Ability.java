package battle.ability;

public abstract class Ability {
    protected String name;
    protected int maxCooldown;
    protected int currentCooldown = 0;

    public Ability(String name, int maxCooldown) {
        this.name = name;
        this.maxCooldown = maxCooldown;
    }

    public boolean isReady() {
        return currentCooldown <= 0;
    }

    public void triggerCooldown() {
        currentCooldown = maxCooldown;
    }

    public void tick() {
        currentCooldown = Math.max(0, currentCooldown - 1);
    }

    public String getName() {
        return name;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public void setName(String name) {
        this.name = name;
    }
}