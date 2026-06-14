package com.here.hereroleplay.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        if (meta != null) {
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', name));
        }
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        if (meta != null) {
            List<String> coloredLore = Arrays.stream(lore)
                    .map(line -> org.bukkit.ChatColor.translateAlternateColorCodes('&', line))
                    .toList();
            meta.setLore(coloredLore);
        }
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (meta != null) {
            List<String> coloredLore = lore.stream()
                    .map(line -> org.bukkit.ChatColor.translateAlternateColorCodes('&', line))
                    .toList();
            meta.setLore(coloredLore);
        }
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        if (meta != null && glowing) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
