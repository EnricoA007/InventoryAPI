package net.cytocloud.inventory.api.gui.button;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cytocloud.inventory.api.serialization.ItemSerialization;
import net.cytocloud.serialization.Serializable;
import net.cytocloud.serialization.SerializationField;
import net.cytocloud.serialization.api.AutoSerializable;
import lombok.Getter;
import lombok.Setter;
import net.cytocloud.serialization.util.json.JsonString;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@Getter
public class Button extends AutoSerializable implements Serializable.FieldModifier {

    public static NamespacedKey BUTTON_NAME = NamespacedKey.fromString("button.name");

    @SerializationField(fieldModifier = true)
    private @NotNull ItemStack item;
    @SerializationField
    private @NotNull String name;
    @Setter
    private @Nullable ButtonHandler handler;
    @SerializationField
    private int slot;

    public Button(@NotNull ItemStack item, int slot, @NotNull String name, @NotNull ButtonHandler handler) {
        this.item = item;
        this.slot = slot;
        this.name = name;
        this.handler = handler;

        markItemStack();
    }

    public Button(@NotNull JsonObject json){
        super(json);
    }

    public void markItemStack() {
        Button.markItemStack(this);
    }

    /// STATIC METHODS

    public static void markItemStack(@NotNull Button button) {
        markItemStack(button.getName(), button.getItem());
    }

    public static void markItemStack(@NotNull String buttonName, @NotNull ItemStack item) {
        ItemMeta m = item.getItemMeta();

        m.getPersistentDataContainer().set(BUTTON_NAME, PersistentDataType.STRING, buttonName);

        item.setItemMeta(m);
    }

    public static boolean isButtonMarked(@NotNull ItemStack item) {
        if(!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(BUTTON_NAME);
    }

    public static @Nullable String getMarkedName(@NotNull ItemStack item) {
        if(!isButtonMarked(item)) return null;
        return item.getItemMeta().getPersistentDataContainer().get(BUTTON_NAME, PersistentDataType.STRING);
    }

    @Override
    public JsonObject serializeSingle(Field field) throws NotModify {
        return new Gson().fromJson(ItemSerialization.serialize(getItem()), JsonObject.class);
    }

    @Override
    public Object deserializeSingle(Field field, JsonObject object) throws NotModify {
        return ItemSerialization.deserialize(new Gson().toJson(object));
    }

}
