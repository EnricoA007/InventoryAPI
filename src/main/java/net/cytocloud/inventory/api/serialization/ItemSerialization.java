package net.cytocloud.inventory.api.serialization;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class ItemSerialization {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter()).create();

    public static @NotNull String serialize(@NotNull ItemStack item) {
        return gson.toJson(item);
    }

    public static @NotNull ItemStack deserialize(@NotNull String item) {
        return gson.fromJson(item, ItemStack.class);
    }

    public static @NotNull JsonObject serializeList(@NotNull List<ItemStack> items) {
        JsonObject obj = new JsonObject();

        JsonArray array = new JsonArray();

        items.forEach(itemStack -> array.add(serialize(itemStack)));

        obj.add("items", array);

        return obj;
    }

    public static @NotNull List<ItemStack> deserializeList(@NotNull JsonObject json) {
        List<ItemStack> items = new ArrayList<>();

        json.get("items").getAsJsonArray().forEach(e -> items.add(deserialize(e.getAsString())));

        return items;
    }

    private static class ConfigurationSerializableAdapter implements JsonSerializer<ConfigurationSerializable>, JsonDeserializer<ConfigurationSerializable> {

        final Type objectStringMapType = new TypeToken<Map<String, Object>>() {}.getType();

        @Override
        public ConfigurationSerializable deserialize(
                JsonElement json,
                Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException
        {
            final Map<String, Object> map = new LinkedHashMap<>();

            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                final JsonElement value = entry.getValue();
                final String name = entry.getKey();

                if (value.isJsonObject() && value.getAsJsonObject().has(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                    map.put(name, this.deserialize(value, value.getClass(), context));
                } else {
                    map.put(name, context.deserialize(value, Object.class));
                }
            }

            return ConfigurationSerialization.deserializeObject(map);
        }

        @Override
        public JsonElement serialize(
                ConfigurationSerializable src,
                Type typeOfSrc,
                JsonSerializationContext context)
        {
            final Map<String, Object> map = new LinkedHashMap<>();
            map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(src.getClass()));
            map.putAll(src.serialize());
            return context.serialize(map, objectStringMapType);
        }
    }

}
