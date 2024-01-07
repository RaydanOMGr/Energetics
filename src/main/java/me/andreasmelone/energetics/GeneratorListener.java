package me.andreasmelone.energetics;

import com.burchard36.bukkit.energy.BukkitEnergy;
import com.burchard36.bukkit.energy.IEnergyStorage;
import me.andreasmelone.amutillib.blocks.AMBlock;
import me.andreasmelone.amutillib.events.ServerTickEvent;
import me.andreasmelone.amutillib.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GeneratorListener implements Listener {
    private final Energetics plugin;
    public GeneratorListener(Energetics plugin) {
        this.plugin = plugin;
    }

    List<Location> generators = new ArrayList<>();

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        AMBlock.getBlocksInChunk(plugin, event.getChunk()).forEach(block -> {
            if(Blocks.GENERATOR_BLOCK.get().compareTo(block)) {
                registerGenerator(block.getLocation());
            }
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        AMBlock.getBlocksInChunk(plugin, event.getChunk()).forEach(block -> {
            if(Blocks.GENERATOR_BLOCK.get().compareTo(block)) {
                unregisterGenerator(block.getLocation());
            }
        });
    }

    @EventHandler
    public void onServerTick(ServerTickEvent event) {
        generators.forEach(location -> {
            Block block = location.getBlock();
            if(!Blocks.GENERATOR_BLOCK.get().compareTo(block)) return;

            Optional<BukkitEnergy> energy = plugin.getEnergyFactory().getEnergyBlock(block);
            if(energy.isEmpty()) return;

            IEnergyStorage bukkitEnergy = energy.get();
            bukkitEnergy.generateEnergy(20);

            if(!(block.getState() instanceof Furnace furnace)) return;
            ItemStack item = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Util.transform(
                    "&rEnergy: " + bukkitEnergy.getStoredEnergy() + "/" + bukkitEnergy.getMaxEnergyStorage())
            );
            item.setItemMeta(meta);
            furnace.getInventory().setSmelting(item);
            block.getState().update();
        });
    }

    public void registerGenerator(Location location) {
        generators.add(location);
        Optional<BukkitEnergy> energy = plugin.getEnergyFactory().getEnergyBlock(location.getBlock());
        if(energy.isEmpty()) energy = plugin.getEnergyFactory().createEnergyBlock(location.getBlock());
        if(energy.isEmpty()) unregisterGenerator(location);
        IEnergyStorage bukkitEnergy = energy.get();
        bukkitEnergy.setMaxEnergyStored(Blocks.maxEnergyStored);
    }

    public void unregisterGenerator(Location location) {
        generators.remove(location);
    }
}
