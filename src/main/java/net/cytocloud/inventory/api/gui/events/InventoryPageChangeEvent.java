package net.cytocloud.inventory.api.gui.events;

import lombok.Getter;
import lombok.Setter;
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
public class InventoryPageChangeEvent extends GUIInventoryEvent implements Cancellable {

    @Setter
    private boolean cancelled = false;
    private final Player player;
    private final InventoryPage lastPage, nextPage;

    /**
     * @param player The player which executed this event
     * @param lastPage The page where the event was executed ({@link InventoryView#getBottomInventory()}
     * @param nextPage The changed page ({@link InventoryView#getTopInventory()})
     * @apiNote This event can be cancelled (When cancelled no page change will happen)
     */
    public InventoryPageChangeEvent(@NotNull Player player, @NotNull InventoryPage lastPage, @NotNull InventoryPage nextPage) {
        super(lastPage.getGUI(), new InventoryView() {
            @Override
            public @NotNull Inventory getTopInventory() {
                return nextPage.getInventory();
            }

            @Override
            public @NotNull Inventory getBottomInventory() {
                return lastPage.getInventory();
            }

            @Override
            public @NotNull HumanEntity getPlayer() {
                return player;
            }

            @Override
            public @NotNull InventoryType getType() {
                return nextPage.getInventory().getType();
            }

            @Override
            public @NotNull String getTitle() {
                return PlainTextComponentSerializer.plainText().serialize(nextPage.getInventory().getType().defaultTitle());
            }
        });

        this.player=player;
        this.lastPage=lastPage;
        this.nextPage=nextPage;
    }

}
