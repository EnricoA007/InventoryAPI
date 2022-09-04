package net.cytocloud.inventory.internal;

import net.cytocloud.inventory.api.InventoryAPI;
import net.cytocloud.inventory.api.gui.button.Button;
import net.cytocloud.inventory.api.gui.events.InventoryButtonClickEvent;
import net.cytocloud.inventory.api.gui.page.InventoryPage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryPickupItemEvent(InventoryPickupItemEvent e){
        InventoryPage page = InventoryAPI.getPageFromInventory(e.getInventory());
        if(page == null) return;

        page.getPageEventManager().callEvent(e);
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent e){
        if(e.getInventory().equals(e.getWhoClicked().getInventory())) return;

        InventoryPage page = InventoryAPI.getPageFromInventory(e.getInventory());
        if(page == null) return;

        page.getPageEventManager().callEvent(e);
    }

    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent e){
        InventoryPage page = InventoryAPI.getPageFromInventory(e.getDestination());
        if(page == null) return;

        page.getPageEventManager().callEvent(e);
    }

    @EventHandler
    public void onInventoryInteractEvent(InventoryInteractEvent e){
        if(e.getInventory().equals(e.getWhoClicked().getInventory())) return;

        InventoryPage page = InventoryAPI.getPageFromInventory(e.getInventory());
        if(page == null) return;

        page.getPageEventManager().callEvent(e);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        InventoryPage page = InventoryAPI.getPageFromInventory(e.getInventory());
        if(page == null) return;

        page.getPageEventManager().callEvent(e);
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent e) {
        if(e.getInventory().equals(e.getPlayer().getInventory())) return;

        InventoryPage page = InventoryAPI.getPageFromInventory(e.getInventory());
        if(page == null) return;

        page.getPageEventManager().callEvent(e);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        InventoryPage page = InventoryAPI.getPageFromInventory(e.getInventory());

        if(page == null) return;

        if(e.getCurrentItem() != null && Button.isButtonMarked(e.getCurrentItem())) {
            Button btn = page.getButton(Button.getMarkedName(e.getCurrentItem()));

            InventoryButtonClickEvent c = new InventoryButtonClickEvent((Player) e.getWhoClicked(), page, btn);
            page.getPageEventManager().callEvent(c);

            if(c.isCancelled()) {
                e.setCancelled(true);
                return;
            }

            if(btn.getHandler() != null) {
                e.setCancelled(true);
                btn.getHandler().handle(e, page, btn);
            }

            return;
        }

        page.getPageEventManager().callEvent(e);
    }

}
