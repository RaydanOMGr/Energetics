package me.andreasmelone.energetics;

import com.burchard36.bukkit.BukkitEnergyPlugin;
import com.burchard36.bukkit.capability.EnergyFactory;
import com.burchard36.bukkit.energy.BukkitEnergy;
import com.jeff_media.customblockdata.CustomBlockData;
import me.andreasmelone.amutillib.AMUtilLib;
import me.andreasmelone.amutillib.i18n.I18n;
import me.andreasmelone.amutillib.utils.CommandUtil;
import me.andreasmelone.energetics.blocks.Blocks;
import me.andreasmelone.energetics.blocks.generator.GeneratorListener;
import me.andreasmelone.energetics.commands.DebugCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Energetics extends JavaPlugin {
    private EnergyFactory<BukkitEnergy> energyFactory;
    private GeneratorListener generatorListener;
    private final I18n i18n = new I18n(this);
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.energyFactory = BukkitEnergyPlugin.getInstance().getEnergyFactory(BukkitEnergy.class);

        Items.register();
        Blocks.register();

        generatorListener = new GeneratorListener(this);
        getServer().getPluginManager().registerEvents(generatorListener, this);

        AMUtilLib.getInstance().registerEvents(this);
        AMUtilLib.getInstance().registerCommands(this, "giveitem");

        CommandUtil.registerTabExecutor(this, new DebugCommand(this), "debugenergy");

        CustomBlockData.registerListener(this);
        CustomBlockData.registerListener(BukkitEnergyPlugin.getInstance());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public GeneratorListener getGeneratorListener() {
        return this.generatorListener;
    }

    public EnergyFactory<BukkitEnergy> getEnergyFactory() {
        return this.energyFactory;
    }

    public I18n getI18n() {
        return this.i18n;
    }

    public static Energetics getInstance() {
        return getPlugin(Energetics.class);
    }
}
