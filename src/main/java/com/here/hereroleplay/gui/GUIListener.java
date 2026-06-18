package com.here.hereroleplay.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        
        InventoryHolder holder = event.getInventory().getHolder();
        
        if (holder instanceof CustomGUI gui) {
            // Cancel the event by default to prevent stealing items
            event.setCancelled(true);
            
            // If they clicked inside the custom GUI itself, pass the event
            if (event.getClickedInventory().equals(event.getInventory())) {
                gui.onClick(event);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof CustomGUI) {
            event.setCancelled(true);
        }
    }
}
