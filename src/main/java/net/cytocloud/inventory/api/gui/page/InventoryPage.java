package net.cytocloud.inventory.api.gui.page;

import com.google.gson.JsonObject;
import lombok.Setter;
import net.cytocloud.inventory.api.gui.page.event.PageEventManager;
import net.cytocloud.serialization.Serializable;
import net.cytocloud.serialization.SerializationField;
import net.cytocloud.serialization.api.AutoSerializable;
import net.cytocloud.serialization.util.ListSerializable;
import lombok.AccessLevel;
import lombok.Getter;
import net.cytocloud.inventory.api.gui.InventoryGUI;
import net.cytocloud.inventory.api.gui.button.Button;
import net.cytocloud.inventory.api.gui.button.ButtonHandler;
import net.cytocloud.inventory.api.serialization.InventorySerialization;
import net.cytocloud.inventory.internal.GUIHolder;
import net.cytocloud.serialization.util.json.JsonString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class InventoryPage extends AutoSerializable implements Serializable.FieldModifier {

    @SerializationField(fieldModifier = true)
    private @NotNull Inventory inventory;
    @SerializationField(fieldModifier = true)
    private @NotNull Component title;
    private @Nullable @Setter InventoryGUI GUI;
    @SerializationField(fieldModifier = true) @Getter(AccessLevel.NONE)
    private @NotNull List<Button> buttonList;
    @Setter
    private @Nullable PageEventManager pageEventManager = new PageEventManager();

    public InventoryPage(@NotNull InventoryGUI gui, @NotNull Component title, int size) {
        Objects.requireNonNull(gui);
        Objects.requireNonNull(title);

        this.inventory = Bukkit.createInventory(new GUIHolder(gui, this), size, title);
        this.GUI = gui;
        this.title = title;
        this.buttonList = new ArrayList<>();
    }

    public InventoryPage(@NotNull InventoryGUI gui, @NotNull String title, int size) {
        this(gui, Component.text(title), size);
    }

    public InventoryPage(JsonObject json) {
        super(json);
    }

    /// BUTTON MANAGEMENT

    /**
     * Add a button to the inventory page
     * @param item The item
     * @param slot The slot
     * @param name The name of the button
     * @param handler The handler which handles clicking events
     * @return An instance of this
     */
    public @NotNull InventoryPage addButton(@NotNull ItemStack item, int slot, @NotNull String name, @NotNull ButtonHandler handler) {
        return this.addButton(new Button(item, slot, name, handler));
    }

    /**
     * Add a button to the inventory page
     * @param button The button
     * @return An instance of this
     */
    public @NotNull InventoryPage addButton(@NotNull Button button) {
        getInventory().setItem(button.getSlot(), button.getItem());
        this.buttonList.add(button);
        return this;
    }

    /**
     * Get a button from its name
     * @param name The name of the button
     * @return The button or null when it isn't present
     */
    public @Nullable Button getButton(@NotNull String name) {
        return getButtons().stream().filter(button -> button.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Get a button from its slot
     * @param slot The slot of the button
     * @return The button or null when it isn't present
     */
    public @Nullable Button getButton(int slot) {
        return getButtons().stream().filter(button -> button.getSlot() == slot).findFirst().orElse(null);
    }

    /**
     * Get a copy of all buttons
     * @return A list with all added buttons
     */
    public @NotNull List<Button> getButtons() {
        return new ArrayList<>(buttonList);
    }

    /**
     * Get the index of this page
     * @return the index
     */
    public int getIndex() {
        return getGUI().getPageIndex(this);
    }

    /// SERIALIZATION

    @Override
    public @NotNull JsonObject serializeSingle(Field field) throws NotModify {
        // Serialize the Button List
        if(field.getName().equals("buttonList"))
            return ListSerializable.serialize(buttonList);

        // Serialize the title
        if(field.getName().equals("title"))
            return JsonString.single(GsonComponentSerializer.gson().serialize(title));

        //Serialize the inventory
        return InventorySerialization.serialize(this.getInventory(), getTitle());
    }

    @Override
    public @NotNull Object deserializeSingle(Field field, JsonObject json) throws NotModify {
        // Deserialize the Button List
        if(field.getName().equals("buttonList"))
            return ListSerializable.deserialize(json, Button.class);

        // Deserialize the title
        if(field.getName().equals("title"))
            return GsonComponentSerializer.gson().deserialize(JsonString.get(json));

        // Deserialize the inventory
        return InventorySerialization.deserialize(json, new GUIHolder(getGUI(), this));
    }


}
