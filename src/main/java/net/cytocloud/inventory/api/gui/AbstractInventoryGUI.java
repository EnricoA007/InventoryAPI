package net.cytocloud.inventory.api.gui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cytocloud.inventory.api.gui.button.Button;
import net.cytocloud.inventory.api.gui.button.ButtonHandler;
import net.cytocloud.inventory.api.gui.page.InventoryPage;
import net.cytocloud.inventory.api.gui.page.event.PageEventManager;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class AbstractInventoryGUI {

    ///OVERRIDABLE METHODS

    /**
     * Where is the serialized gui stored
     * @return The file
     */
    public abstract @NotNull File storageFile();

    /**
     * This is used to provide event accessibility (also on deserialize)
     * @param page The page where the events should be located
     * @return The page event manager
     */
    public abstract @Nullable PageEventManager getEventManager(@NotNull InventoryPage page);

    /**
     * Get a button handler from the button name
     * @param buttonName The name of the button
     * @return The button handler
     * @apiNote This is used to provide button handlers after a deserialization
     */
    public abstract @Nullable ButtonHandler getButtonHandler(@NotNull String buttonName);

    /**
     * Get the default inventory gui (This will be called when nothing is stored)
     * @return The default gui
     */
    protected abstract @NotNull InventoryGUI getDefaultGUI();

    ///DON'T OVERRIDE THESE METHODS BELOW

    /**
     * @param gui The inventory gui to save
     * @throws IOException When an io exception occur
     * @return An instance of the entered parameter
     */
    public @NotNull InventoryGUI saveStorage(@NotNull InventoryGUI gui) throws IOException {
        OutputStream out = new FileOutputStream(storageFile());

        out.write(gui.serializeToPrettyJson().getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();

        return gui;
    }

    /**
     * @return The inventory gui from the storage file (or null: when the file doesn't exist)
     */
    public @NotNull InventoryGUI loadStorage() throws IOException {
        InputStream in = new FileInputStream(storageFile());
        byte[] b = IOUtils.toByteArray(in);
        in.close();

        return new InventoryGUI(new Gson().fromJson(new String(b, StandardCharsets.UTF_8), JsonObject.class));
    }

    /**
     * Get the inventory gui
     * @return The inventory gui
     * @throws IOException When an io exception occur (While reading the {@link #storageFile()} or while saving the default gui)
     */
    public @NotNull InventoryGUI getGUI() throws IOException {
        if(!storageFile().exists())
            return saveStorage(getDefaultGUI());

        InventoryGUI gui = loadStorage();

        gui.getPages().forEach(p -> {
            p.setGUI(gui);

            for (Button button : p.getButtons()) {
                button.setHandler(getButtonHandler(button.getName()));
            }

            p.setPageEventManager(getEventManager(p));
        });

        return gui;
    }

    /**
     * Get the inventory gui save
     * @return The inventory gui or null when an IOException occur
     * {@see #getGUI}
     */
    public @Nullable InventoryGUI getGUISafe() {
        try {
            return getGUI();
        } catch (IOException e) {
            return null;
        }
    }

}
