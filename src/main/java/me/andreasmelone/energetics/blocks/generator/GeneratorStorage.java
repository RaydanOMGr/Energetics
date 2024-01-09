package me.andreasmelone.energetics.blocks.generator;

import me.andreasmelone.energetics.Keys;
import org.bukkit.block.Furnace;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GeneratorStorage {
    private int fuelTicks;
    public GeneratorStorage(int fuelTicks) {
        this.fuelTicks = fuelTicks;
    }

    public GeneratorStorage() {}

    public int getFuelTicks() {
        return fuelTicks;
    }

    public void decrementFuelTicks() {
        fuelTicks--;
    }

    public void setFuelTicks(int fuelTicks) {
        this.fuelTicks = fuelTicks;
    }

    public static GeneratorStorage load(PersistentDataContainer pdc) {
        GeneratorStorage storage = new GeneratorStorage();
        storage.fuelTicks = pdc.getOrDefault(Keys.FUEL_TICKS, PersistentDataType.INTEGER, 0);
        return storage;
    }

    public void save(Furnace furnace) {
        PersistentDataContainer pdc = furnace.getPersistentDataContainer();
        pdc.set(Keys.FUEL_TICKS, PersistentDataType.INTEGER, fuelTicks);
    }
}
