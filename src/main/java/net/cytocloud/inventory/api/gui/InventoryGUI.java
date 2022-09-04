package net.cytocloud.inventory.api.gui;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.cytocloud.inventory.api.InventoryAPI;
import net.cytocloud.inventory.api.gui.button.Button;
import net.cytocloud.inventory.api.gui.button.ButtonHandler;
import net.cytocloud.inventory.api.gui.events.InventoryPageChangeEvent;
import net.cytocloud.inventory.api.gui.page.InventoryPage;
import net.cytocloud.serialization.Serializable;
import net.cytocloud.serialization.SerializationField;
import net.cytocloud.serialization.api.AutoSerializable;
import net.cytocloud.serialization.util.ListSerializable;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryGUI extends AutoSerializable implements Serializable.FieldModifier {

    @SerializationField(fieldModifier = true)
    private @NotNull List<InventoryPage> pages;
    @Getter @Setter @SerializationField
    private int startPage;
    private final List<Button> globalButtons = new ArrayList<>();

    public InventoryGUI() {
        this(0);
    }

    public InventoryGUI(int startPage) {
        this.startPage=startPage;
        this.pages = new ArrayList<>();
    }

    public InventoryGUI(JsonObject json) {
        super(json);
    }


    /**
     * Add a new page to the gui
     * @param title The title of the inventory
     * @param size The size of the inventory
     * @return An instance of the page
     * @see #addPage(InventoryPage)
     * @throws NullPointerException When the entered parameter is null
     */
    public @NotNull InventoryPage addPage(@NotNull Component title, int size){
        return addPage(new InventoryPage(this, title, size));
    }

    /**
     * Add a new page to the gui
     * @param title The title of the inventory
     * @param size The size of the inventory
     * @return An instance of the page
     * @see #addPage(InventoryPage)
     * @throws NullPointerException When the entered parameter is null
     */
    public @NotNull InventoryPage addPage(@NotNull String title, int size){
        return addPage(new InventoryPage(this, title, size));
    }

    /**
     * Add a new page to the gui
     * @param page An inventory page
     * @return The entered parameter
     * @throws NullPointerException When the entered parameter is null
     */
    public @NotNull InventoryPage addPage(@NotNull InventoryPage page) {
        Objects.requireNonNull(page);
        this.pages.add(page);

        getGlobalButtons().forEach(page::addButton);

        return page;
    }

    /**
     * Add a button to the inventory page
     * @param item The item
     * @param slot The slot
     * @param name The name of the button
     * @param handler The handler which handles clicking events
     * @return An instance of this
     */
    public @NotNull InventoryGUI addGlobalButton(@NotNull ItemStack item, int slot, @NotNull String name, @NotNull ButtonHandler handler) {
        return this.addGlobalButton(new Button(item, slot, name, handler));
    }

    /**
     * Add a button to the inventory page
     * @param button The button
     * @return An instance of this
     * @apiNote A global {@link Button} is a button that exists on every page
     */
    public @NotNull InventoryGUI addGlobalButton(@NotNull Button button) {
        getPages().forEach(p -> p.addButton(button));
        globalButtons.add(button);

        return this;
    }

    /**
     * Get a list of all registered global buttons
     * @return A copy of all global buttons
     * @apiNote A global {@link Button} is a button that exists on every page
     */
    public List<Button> getGlobalButtons() {
        return new ArrayList<>(globalButtons);
    }

    /**
     * Open the gui to a player (The {@link #getStartPage()} page will be open)
     * @param p The player
     * @see #open(Player, int)
     */
    public void open(@NotNull Player p) {
        this.open(p, getStartPage());
    }

    /**
     * Open the gui to a player
     * @param p The player
     * @param page The page index (0 for the first page)
     * @throws IndexOutOfBoundsException If the page index is out of range
     * @see #open(Player, InventoryPage)
     */
    public void open(@NotNull Player p, int page) {
        this.open(p, pages.get(page));
    }

    /**
     * Open the gui to a player
     * @param p The player
     * @param page The page
     */
    public void open(@NotNull Player p, @NotNull InventoryPage page){
        Objects.requireNonNull(p);
        Objects.requireNonNull(page);

        InventoryPage open = InventoryAPI.getOpen(p);
        if(open != null) {
            InventoryPageChangeEvent e = new InventoryPageChangeEvent(p, open, page);
            Bukkit.getServer().getPluginManager().callEvent(e);
            getPages().forEach(inventoryPage -> inventoryPage.getPageEventManager().callEvent(e));

            if(e.isCancelled()) return;
        }

        p.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
        p.openInventory(page.getInventory());
    }

    /**
     * Get a list of all registered pages
     * @return A copy of all pages
     */
    public @NotNull List<InventoryPage> getPages() {
        return new ArrayList<>(pages);
    }

    /**
     * @param index The page index
     * @return The inventory page
     * @throws IndexOutOfBoundsException – if the index is out of range (index < 0 || index >= size())
     * @see #pagesSize()
     */
    public @NotNull InventoryPage getPage(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return pages.get(index);
    }

    /**
     * @param page The page
     * @return The page index
     * @throws IllegalArgumentException When the entered page wasn't found
     */
    public int getPageIndex(@NotNull InventoryPage page) {
        for (int i = 0; i < pagesSize(); i++) {
            if(getPage(i).equals(page)) return i;
        }

        throw new IllegalArgumentException("The entered page wasn't found");
    }

    /**
     * The number of existing pages
     * @return The size of the pages
     */
    public int pagesSize() {
        return pages.size();
    }

    /**
     * Check if the entered player has the gui open
     * @param p The player
     * @return When the player has the gui open
     */
    public boolean hasPageOpen(@NotNull Player p) {
        return InventoryAPI.hasOpen(p, this);
    }

    /**
     * Get the page which has the player open
     * @param p The player
     * @return the page or null when nothing is open
     * @see #hasPageOpen(Player)
     */
    public @Nullable InventoryPage getPageOpen(@NotNull Player p) {
        return InventoryAPI.getOpen(p, this);
    }

    @Override
    public JsonObject serializeSingle(Field field) {
        return ListSerializable.serialize(getPages());
    }

    @Override
    public Object deserializeSingle(Field field, JsonObject object) {
        return ListSerializable.deserialize(object, InventoryPage.class);
    }


}
