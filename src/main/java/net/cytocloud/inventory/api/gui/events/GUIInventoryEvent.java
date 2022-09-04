package net.cytocloud.inventory.api.gui.events;

import lombok.Getter;
import net.cytocloud.inventory.api.gui.InventoryGUI;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class GUIInventoryEvent extends InventoryEvent {

    @Getter
    private final InventoryGUI GUI;

    public GUIInventoryEvent(@NotNull InventoryGUI gui, @NotNull InventoryView view) {
        super(view);

        this.GUI=gui;
    }

}
