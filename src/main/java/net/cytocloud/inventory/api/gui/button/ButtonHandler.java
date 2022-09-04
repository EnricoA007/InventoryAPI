package net.cytocloud.inventory.api.gui.button;

import net.cytocloud.inventory.api.gui.page.InventoryPage;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ButtonHandler {

    void handle(@NotNull InventoryClickEvent e, @NotNull InventoryPage page, @NotNull Button btn);

}
