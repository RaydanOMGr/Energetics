package me.andreasmelone.energetics;

import com.burchard36.bukkit.energy.BukkitEnergy;
import com.burchard36.bukkit.energy.IEnergyStorage;
import com.google.common.collect.ImmutableList;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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

            if(!(block.getState() instanceof Furnace furnace)) return;

            bukkitEnergy.setMaxEnergyStored(Blocks.maxEnergyStored);

            furnace.setCookTimeTotal(bukkitEnergy.getMaxEnergyStorage());
            furnace.setCookTime((short) bukkitEnergy.getStoredEnergy());
            furnace.update(true, false);

            PersistentDataContainer pdc = furnace.getPersistentDataContainer();
            int fuelTicks = pdc.getOrDefault(
                    Keys.FUEL_TICKS,
                    PersistentDataType.INTEGER,
                    0
            );

            if(fuelTicks > 0) {
                fuelTicks--;
                pdc.set(Keys.FUEL_TICKS, PersistentDataType.INTEGER, fuelTicks);
                bukkitEnergy.generateEnergy(20);
            } else {
                ItemStack fuel = furnace.getInventory().getFuel();
                if(fuel != null && fuel.getType() == Material.COAL) {
                    fuelTicks = 200;
                    pdc.set(Keys.FUEL_TICKS, PersistentDataType.INTEGER, fuelTicks);

                    furnace.getInventory().setFuel(fuel);
                    furnace.setBurnTime((short)((short) 1600 * (double) fuelTicks / 200));
                    furnace.update(true, false);

                    if(fuel.getAmount() > 1) fuel.setAmount(fuel.getAmount() - 1);
                    else furnace.getInventory().setFuel(null);

                    bukkitEnergy.generateEnergy(20);
                }
            }

            Material itemMaterial = Material.RED_STAINED_GLASS_PANE;
            if((double) bukkitEnergy.getStoredEnergy() / bukkitEnergy.getMaxEnergyStorage() > 0.5)
                itemMaterial = Material.ORANGE_STAINED_GLASS_PANE;
            if(bukkitEnergy.getStoredEnergy() >= bukkitEnergy.getMaxEnergyStorage())
                itemMaterial = Material.GREEN_STAINED_GLASS_PANE;

            ItemStack item = new ItemStack(itemMaterial);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Util.transform(
                    "&rEnergy: " + bukkitEnergy.getStoredEnergy() + "/" + bukkitEnergy.getMaxEnergyStorage())
            );
            item.setItemMeta(meta);
            furnace.getInventory().setResult(item);
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getInventory().getHolder() instanceof Furnace furnace)) return;
        if(!Blocks.GENERATOR_BLOCK.get().compareTo(furnace.getBlock())) return;
        if(event.getSlotType() == InventoryType.SlotType.RESULT) event.setCancelled(true);
    }

    public void registerGenerator(Location location) {
        generators.add(location);
        Optional<BukkitEnergy> energy = plugin.getEnergyFactory().getEnergyBlock(location.getBlock());
        if(energy.isEmpty()) energy = plugin.getEnergyFactory().createEnergyBlock(location.getBlock());
        if(energy.isEmpty()) {
            plugin.getLogger().warning("Failed to register generator at " + location);
            unregisterGenerator(location);
            return;
        }
        IEnergyStorage bukkitEnergy = energy.get();
        bukkitEnergy.setMaxEnergyStored(Blocks.maxEnergyStored);

        if(!(location.getBlock().getState() instanceof Furnace furnace)) return;
        PersistentDataContainer pdc = furnace.getPersistentDataContainer();
        int fuelTicks = pdc.getOrDefault(
                Keys.FUEL_TICKS,
                PersistentDataType.INTEGER,
                0
        );

        furnace.setBurnTime((short) fuelTicks);
        furnace.update(true, false);
    }

    public void unregisterGenerator(Location location) {
        generators.remove(location);
    }

    public List<Location> getGenerators() {
        return ImmutableList.copyOf(generators);
    }
}
