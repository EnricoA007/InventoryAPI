package net.cytocloud.inventory.internal;

import lombok.Getter;
import net.cytocloud.inventory.api.gui.InventoryGUI;
import net.cytocloud.inventory.api.gui.page.InventoryPage;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
public class GUIHolder implements InventoryHolder {

    private final InventoryGUI GUI;
    private final InventoryPage page;

    public GUIHolder(InventoryGUI gui, InventoryPage page) {
        this.GUI=gui;
        this.page=page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return page.getInventory();
    }

}