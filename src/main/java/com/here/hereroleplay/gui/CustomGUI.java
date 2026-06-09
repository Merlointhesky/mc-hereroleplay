package com.here.hereroleplay.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface CustomGUI extends InventoryHolder {

    /**
     * Called when a player clicks an item in this GUI.
     */
    void onClick(InventoryClickEvent event);
    
    @Override
    Inventory getInventory();
}
