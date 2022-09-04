package net.cytocloud.inventory.test;

import net.cytocloud.inventory.api.gui.AbstractInventoryGUI;
import net.cytocloud.inventory.api.gui.InventoryGUI;
import net.cytocloud.inventory.api.gui.button.ButtonHandler;
import net.cytocloud.inventory.api.gui.page.InventoryPage;
import net.cytocloud.inventory.api.gui.page.event.PageEventManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class TestGUI extends AbstractInventoryGUI {

    @Override
    public @NotNull File storageFile() {
        return new File("plugins/testgui.json");
    }

    @Override
    public @Nullable PageEventManager getEventManager(@NotNull InventoryPage page) {
        return new PageEventManager().enableCancelling();
    }

    @Override
    public @Nullable ButtonHandler getButtonHandler(@NotNull String buttonName) {
        switch(buttonName){
            case "next_page" -> {
                return (e, page, btn) -> page.getGUI().open((Player) e.getWhoClicked(), 1);
            }
            case "prev_page" -> {
                return (e, page, btn) -> page.getGUI().open((Player) e.getWhoClicked(), 0);
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    protected @NotNull InventoryGUI getDefaultGUI() {
        InventoryGUI gui = new InventoryGUI();

        InventoryPage page0 = gui.addPage("Page 0", 27);
        page0.getInventory().setItem(0, new ItemStack(Material.EMERALD_BLOCK));
        page0.addButton(new ItemStack(Material.STICK), 8, "next_page", getButtonHandler("next_page"));
        page0.setPageEventManager(getEventManager(page0));

        InventoryPage page1 = gui.addPage("Page 1", 27);
        page1.getInventory().setItem(0, new ItemStack(Material.DIAMOND_BLOCK));
        page1.addButton(new ItemStack(Material.BLAZE_ROD), 8, "prev_page", getButtonHandler("prev_page"));
        page1.setPageEventManager(getEventManager(page1));

        return gui;
    }

}
