package me.andreasmelone.energetics;

import me.andreasmelone.amutillib.items.AMItem;
import me.andreasmelone.amutillib.items.ItemBuilder;
import me.andreasmelone.amutillib.items.ItemRegister;
import me.andreasmelone.amutillib.registry.RegisteredObject;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {
    static Energetics plugin = Energetics.getInstance();

    public static final RegisteredObject<AMItem> GENERATOR_BLOCK = ItemRegister.getInstance().register(
            ItemBuilder.createBuilder(plugin, "generator_block")
                    .setName("&rGenerator Block")
                    .setLore("&r Generates &220 &eBE&f/&4T")
                    .setMaterial(Material.FURNACE)
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
    }
}
