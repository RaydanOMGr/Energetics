package me.andreasmelone.energetics.commands;

import com.burchard36.bukkit.BukkitEnergyPlugin;
import com.burchard36.bukkit.energy.IEnergyStorage;
import com.jeff_media.customblockdata.CustomBlockData;
import me.andreasmelone.energetics.Energetics;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DebugCommand implements TabExecutor {
    private final Energetics plugin;
    public DebugCommand(Energetics plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            sender.sendMessage("Usage: /" + label + " <subcommand>");
            return true;
        }

        switch(args[0].toLowerCase()) {
            case "generators":
                sender.sendMessage("Generators: " + plugin.getGeneratorListener().getGenerators());
                break;
            case "energy":
                List<IEnergyStorage> energyBlocks = new ArrayList<>();
                if(!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use this command!");
                    return true;
                }
                Chunk chunk = player.getLocation().getChunk();
                CustomBlockData.getBlocksWithCustomData(BukkitEnergyPlugin.getInstance(), chunk).forEach(block -> {
                    if(block.getType().isBlock()) {
                        energyBlocks.add(plugin.getEnergyFactory().getEnergyBlock(block).orElse(null));
                    }
                });
                energyBlocks.removeIf(Objects::isNull);
                sender.sendMessage("Energy: " + energyBlocks);
                break;
            case "clear":
                if(!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use this command!");
                    return true;
                }
                player.sendMessage("Clearing up chunk data...");
                CustomBlockData.getBlocksWithCustomData(BukkitEnergyPlugin.getInstance(), player.getLocation().getChunk()).forEach(block -> {
                    CustomBlockData data = new CustomBlockData(block, BukkitEnergyPlugin.getInstance());
                    block.setType(Material.AIR);
                    data.clear();
                });
                CustomBlockData.getBlocksWithCustomData(plugin, player.getLocation().getChunk()).forEach(block -> {
                    CustomBlockData data = new CustomBlockData(block, plugin);
                    block.setType(Material.AIR);
                    data.clear();
                });
                plugin.getGeneratorListener().getGenerators().forEach((location, storage) -> {
                    plugin.getGeneratorListener().unregisterGenerator(location);
                });

                player.sendMessage("Done!");

                plugin.getLogger().severe("SEVERE: PLAYER " + player.getName() + " CLEARED CHUNK DATA AT " + player.getLocation().getChunk());
                plugin.getLogger().severe("PLEASE CHECK IF THIS WAS INTENTIONAL!");
                break;
            case "reloadchunk":
                if(!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use this command!");
                    return true;
                }
                player.sendMessage("Reloading chunk...");
                player.getLocation().getChunk().unload(true);
                player.getLocation().getChunk().load(true);
                player.sendMessage("Done!");

                plugin.getLogger().warning("WARNING: PLAYER " + player.getName() + " RELOADED CHUNK AT " + player.getLocation().getChunk());
                break;
            default:
                sender.sendMessage("Unknown subcommand!");
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tabComplete = new ArrayList<>();
        tabComplete.add("generators");
        tabComplete.add("energy");
        tabComplete.add("clear");
        tabComplete.add("reloadchunk");
        return tabComplete;
    }
}
