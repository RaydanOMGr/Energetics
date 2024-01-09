package me.andreasmelone.energetics;

import com.burchard36.bukkit.energy.BukkitEnergy;
import me.andreasmelone.amutillib.i18n.TranslationKey;
import me.andreasmelone.amutillib.items.AMItem;
import me.andreasmelone.amutillib.items.ItemBuilder;
import me.andreasmelone.amutillib.items.ItemRegister;
import me.andreasmelone.amutillib.registry.RegisteredObject;
import me.andreasmelone.amutillib.utils.Util;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class Items {
    static Energetics plugin = Energetics.getInstance();

    public static final RegisteredObject<AMItem> GENERATOR_BLOCK = ItemRegister.getInstance().register(
            ItemBuilder.createBuilder(plugin, "generator_block")
                    .setName("&rGenerator Block")
                    .setLore("&r&fGenerates &e20 BE/t")
                    .setMaterial(Material.FURNACE)
                    .build()
    );

    public static final RegisteredObject<AMItem> ENERGY_REMOVER = ItemRegister.getInstance().register(
            ItemBuilder.createBuilder(plugin, "energy_remover")
                    .setName("&rEnergy Remover")
                    .setLore("&r&fRemoves all energy from a block")
                    .setMaterial(Material.BLAZE_ROD)
                    .build()
    );

    public static void register() {
        // GENERATOR BLOCK START

        GENERATOR_BLOCK.get().onCreateItemStack(event -> {
            event.getItemStack().addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            ItemMeta meta = event.getItemStack().getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            event.getItemStack().setItemMeta(meta);
        });

        // GENERATOR BLOCK END

        // ENERGY REMOVER START

        ENERGY_REMOVER.get().onCreateItemStack(event -> {
            event.getItemStack().addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            ItemMeta meta = event.getItemStack().getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            event.getItemStack().setItemMeta(meta);
        });

        ENERGY_REMOVER.get().onInteract(event -> {
            if(!Util.isRightClick(event.getAction())) return;
            event.setCancelled(true);

            if(event.getClickedBlock() == null) return;
            Optional<BukkitEnergy> energy = plugin.getEnergyFactory().getEnergyBlock(event.getClickedBlock());
            if(energy.isEmpty()) {
                event.getPlayer().sendMessage(plugin.getI18n().getTransformed("energy_remover.not_energy_block"));
                return;
            }

            int energyToBurn = energy.get().getStoredEnergy();
            energy.get().burnEnergy(energyToBurn);
            event.getPlayer().sendMessage(plugin.getI18n().getTransformed(
                    "energy_remover.removed_energy",
                    TranslationKey.of("%energy%", energyToBurn)
            ));
        });

        // ENERGY REMOVER END
    }
}
