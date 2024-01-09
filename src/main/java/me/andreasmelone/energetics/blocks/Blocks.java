package me.andreasmelone.energetics.blocks;

import com.burchard36.bukkit.energy.BukkitEnergy;
import com.burchard36.bukkit.energy.IEnergyStorage;
import me.andreasmelone.amutillib.blocks.AMBlock;
import me.andreasmelone.amutillib.blocks.BlockRegister;
import me.andreasmelone.amutillib.registry.RegisteredObject;
import me.andreasmelone.energetics.Energetics;
import me.andreasmelone.energetics.Items;

import java.util.Optional;

public class Blocks {
    static Energetics plugin = Energetics.getInstance();

    public static final RegisteredObject<AMBlock> GENERATOR_BLOCK = BlockRegister.getInstance().register(
            AMBlock.from(Items.GENERATOR_BLOCK.get())
    );
    public static final int maxEnergyStored = 10000;

    public static void register() {
        // GENERATOR BLOCK START

        GENERATOR_BLOCK.get().onBlockPlace(event -> {
            plugin.getGeneratorListener().registerGenerator(event.getBlock().getLocation());
        });

        GENERATOR_BLOCK.get().onBlockBreak(event -> {
            plugin.getGeneratorListener().unregisterGenerator(event.getBlock().getLocation());
            Optional<BukkitEnergy> energy = plugin.getEnergyFactory().getEnergyBlock(event.getBlock());
            if(energy.isEmpty()) return;
            IEnergyStorage bukkitEnergy = energy.get();
            bukkitEnergy.burnEnergy(bukkitEnergy.getStoredEnergy());
        });

        // GENERATOR BLOCK END
    }
}
