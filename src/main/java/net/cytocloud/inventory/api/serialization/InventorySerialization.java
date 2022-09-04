package net.cytocloud.inventory.api.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventorySerialization {

    /**
     * Serialize an inventory to json
     * @param inv The inventory
     * @param title The title of the inventory
     * @return The serialized inventory
     */
    public static @NotNull JsonObject serialize(@NotNull Inventory inv, @NotNull Component title) {
        JsonObject main = new JsonObject();
        JsonArray contents = new JsonArray();

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if(item == null) continue;

            JsonObject content = new JsonObject();

            content.addProperty("slot", i);
            content.add("item", new Gson().fromJson(ItemSerialization.serialize(item), JsonObject.class));

            contents.add(content);
        }

        main.addProperty("size", inv.getSize());
        main.addProperty("title", GsonComponentSerializer.gson().serialize(title));
        main.add("content", contents);

        return main;
    }

    /**n
     * Deserialize an inventory to json
     * @param json The serialized json
     * @param holder The holder of the inventory
     * @return The deserialized inventory
     */
    public static Inventory deserialize(@NotNull JsonObject json, @Nullable InventoryHolder holder) {
        int size = json.get("size").getAsInt();
        Component title = GsonComponentSerializer.gson().deserialize(json.get("title").getAsString());

        Inventory inv = Bukkit.createInventory(holder, size, title);

        json.get("content").getAsJsonArray().forEach(jc -> {
            JsonObject content = jc.getAsJsonObject();

            int slot = content.get("slot").getAsInt();
            ItemStack item = ItemSerialization.deserialize(new Gson().toJson(content.get("item").getAsJsonObject()));

            inv.setItem(slot, item);
        });

        return inv;
    }

}
