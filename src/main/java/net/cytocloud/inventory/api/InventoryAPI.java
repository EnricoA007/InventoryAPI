package net.cytocloud.inventory.api;

import net.cytocloud.inventory.api.gui.InventoryGUI;
import net.cytocloud.inventory.api.gui.page.InventoryPage;
import net.cytocloud.inventory.internal.GUIHolder;
import net.cytocloud.inventory.internal.InventoryListener;
import net.cytocloud.inventory.test.TestGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class InventoryAPI extends JavaPlugin {

    private final InventoryListener listener = new InventoryListener();
    private final TestGUI gui = new TestGUI();

    @Override
    public void onEnable() {
        this.getCommand("testgui").setExecutor((sender, command, s, args) -> {

            try {
                gui.getGUI().open((Player)sender);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        });
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(listener);
    }

    public static @Nullable InventoryPage getPageFromInventory(@NotNull Inventory inv) {
        if(inv.getHolder() == null) return null;
        if(!(inv.getHolder() instanceof GUIHolder view)) return null;

        return view.getPage();
    }

    /**
     * @param p The player
     * @return The opened Inventory Page or null when nothing is open
     */
    public static @Nullable InventoryPage getOpen(@NotNull Player p) {
        return getPageFromInventory(p.getOpenInventory().getTopInventory());
    }

    /**
     * Check if the entered player has a {@link InventoryGUI} open
     * @param p The player
     * @return true when it is open
     */
    public static boolean hasOpen(@NotNull Player p) {
        return getOpen(p) != null;
    }

    /**
     * @param p The player
     * @param gui The gui to compare with
     * @return The opened Inventory Page or null when nothing is open
     */
    public static @Nullable InventoryPage getOpen(@NotNull Player p, @NotNull InventoryGUI gui) {
        Inventory inv = p.getOpenInventory().getTopInventory();

        if(inv.getHolder() == null) return null;
        if(!(inv.getHolder() instanceof GUIHolder view)) return null;
        if(!(view.getGUI().equals(gui))) return null;

        return view.getPage();
    }

    /**
     * @param p The player
     * @param gui The inventory gui to compare with
     * @return true when the gui is open
     */
    public static boolean hasOpen(Player p, InventoryGUI gui) {
        return getOpen(p, gui) != null;
    }


}
