package me.andreasmelone.energetics;

import com.burchard36.bukkit.energy.BukkitEnergy;
import com.burchard36.bukkit.energy.IEnergyStorage;
import me.andreasmelone.amutillib.blocks.AMBlock;
import me.andreasmelone.amutillib.blocks.BlockRegister;
import me.andreasmelone.amutillib.registry.RegisteredObject;

import java.util.Optional;

public class Blocks {
    static Energetics plugin = Energetics.getInstance();

    public static final RegisteredObject<AMBlock> GENERATOR_BLOCK = BlockRegister.getInstance().register(
            AMBlock.from(Items.GENERATOR_BLOCK.get())
    );
    public static final int maxEnergyStored = 1000;

    public static void register() {
        // GENERATOR BLOCK START

        GENERATOR_BLOCK.get().onBlockPlace(event -> {
            plugin.getGeneratorListener().registerGenerator(event.getBlock().getLocation());
        });

        GENERATOR_BLOCK.get().onBlockBreak(event -> {
            plugin.getGeneratorListener().unregisterGenerator(event.getBlock().getLocation());
        });

        // GENERATOR BLOCK END
    }
}
