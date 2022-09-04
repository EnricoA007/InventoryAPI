package net.cytocloud.inventory.api.gui.events;

import lombok.Getter;
import lombok.Setter;
import net.cytocloud.inventory.api.gui.button.Button;
import net.cytocloud.inventory.api.gui.page.InventoryPage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

@Getter
public class InventoryButtonClickEvent extends GUIInventoryEvent implements Cancellable {

    @Setter
    private boolean cancelled = false;
    private final Player player;
    private final InventoryPage currentPage;
    private final Button button;

    /**
     * @param player The player which executed this event
     * @param currentPage The page where the event happen ({@link InventoryView#getTopInventory()})
     * @param button The button
     * @apiNote This event can be cancelled (When cancelled no button handler will be called) <br><br> {@link InventoryView#getBottomInventory()} is the player's inventory
     */
    public InventoryButtonClickEvent(@NotNull Player player, @NotNull InventoryPage currentPage, @NotNull Button button) {
        super(currentPage.getGUI(), new InventoryView() {
            @Override
            public @NotNull Inventory getTopInventory() {
                return currentPage.getInventory();
            }

            @Override
            public @NotNull Inventory getBottomInventory() {
                return player.getInventory();
            }

            @Override
            public @NotNull HumanEntity getPlayer() {
                return player;
            }

            @Override
            public @NotNull InventoryType getType() {
                return currentPage.getInventory().getType();
            }

            @Override
            public @NotNull String getTitle() {
                return PlainTextComponentSerializer.plainText().serialize(currentPage.getInventory().getType().defaultTitle());
            }
        });

        this.player=player;
        this.currentPage=currentPage;
        this.button=button;
    }

}

