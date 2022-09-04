package net.cytocloud.inventory.api.gui.page.event;

import net.cytocloud.inventory.api.gui.events.InventoryButtonClickEvent;
import net.cytocloud.inventory.api.gui.events.InventoryPageChangeEvent;
import net.cytocloud.inventory.api.gui.page.InventoryPage;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class PageEventManager {

    private final List<Consumer<InventoryCloseEvent>> closeEvents = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickEvents = new ArrayList<>();
    private final List<Consumer<InventoryOpenEvent>> openEvents = new ArrayList<>();
    private final List<Consumer<InventoryPageChangeEvent>> changePageEvents = new ArrayList<>();
    private final List<Consumer<InventoryButtonClickEvent>> buttonClickEvents = new ArrayList<>();
    private final List<Consumer<InventoryPickupItemEvent>> pickupEvents = new ArrayList<>();
    private final List<Consumer<InventoryDragEvent>> dragEvents = new ArrayList<>();
    private final List<Consumer<InventoryInteractEvent>> interactEvents = new ArrayList<>();
    private final List<Consumer<InventoryMoveItemEvent>> moveItemEvents = new ArrayList<>();
    private boolean cancelling = false;

    public PageEventManager() {}

    /**
     * @param consumer A consumer to handle the {@link InventoryCloseEvent}
     * @return An instance of this
     * @apiNote EventHandler are not possible to serialize (Add the listeners on deserialization)
     * @see InventoryPage#setPageEventManager(PageEventManager)
     */
    public @NotNull PageEventManager onClose(@NotNull Consumer<InventoryCloseEvent> consumer) {
        Objects.requireNonNull(consumer);
        closeEvents.add(consumer);
        return this;
    }

    /**
     * @param consumer A consumer to handle the {@link InventoryClickEvent}
     * @return An instance of this
     * @apiNote EventHandler are not possible to serialize (Add the listeners on deserialization)
     * @see InventoryPage#setPageEventManager(PageEventManager)
     */
    public @NotNull PageEventManager onClick(@NotNull Consumer<InventoryClickEvent> consumer) {
        Objects.requireNonNull(consumer);
        clickEvents.add(consumer);
        return this;
    }

    /**
     * @param consumer A consumer to handle the {@link InventoryOpenEvent}
     * @return An instance of this
     * @apiNote EventHandler are not possible to serialize (Add the listeners on deserialization)
     * @see InventoryPage#setPageEventManager(PageEventManager)
     */
    public @NotNull PageEventManager onOpen(@NotNull Consumer<InventoryOpenEvent> consumer) {
        Objects.requireNonNull(consumer);
        openEvents.add(consumer);
        return this;
    }

    /**
     * @param consumer A consumer to handle the {@link InventoryPageChangeEvent}
     * @return An instance of this
     * @apiNote EventHandler are not possible to serialize (Add the listeners on deserialization)
     * @see InventoryPage#setPageEventManager(PageEventManager)
     */
    public @NotNull PageEventManager onPageChange(@NotNull Consumer<InventoryPageChangeEvent> consumer) {
        Objects.requireNonNull(consumer);
        changePageEvents.add(consumer);
        return this;
    }

    /**
     * @param consumer A consumer to handle the {@link InventoryButtonClickEvent}
     * @return An instance of this
     * @apiNote EventHandler are not possible to serialize (Add the listeners on deserialization)
     * @see InventoryPage#setPageEventManager(PageEventManager)
     */
    public @NotNull PageEventManager onButtonClick(@NotNull Consumer<InventoryButtonClickEvent> consumer) {
        Objects.requireNonNull(consumer);
        buttonClickEvents.add(consumer);
        return this;
    }

    /**
     * @param consumer A consumer to handle the {@link InventoryPickupItemEvent}
     * @return An instance of this
     * @apiNote EventHandler are not possible to serialize (Add the listeners on deserialization)
     * @see InventoryPage#setPageEventManager(PageEventManager)
     */
    public @NotNull PageEventManager onPickup(@NotNull Consumer<InventoryPickupItemEvent> consumer) {
        Objects.requireNonNull(consumer);
        pickupEvents.add(consumer);
        return this;
    }

    /**
     * @param consumer A consumer to handle the {@link InventoryDragEvent}
     * @return An instance of this
     * @apiNote EventHandler are not possible to serialize (Add the listeners on deserialization)
     * @see InventoryPage#setPageEventManager(PageEventManager)
     */
    public @NotNull PageEventManager onDrag(@NotNull Consumer<InventoryDragEvent> consumer) {
        Objects.requireNonNull(consumer);
        dragEvents.add(consumer);
        return this;
    }

    /**
     * @param consumer A consumer to handle the {@link InventoryInteractEvent}
     * @return An instance of this
     * @apiNote EventHandler are not possible to serialize (Add the listeners on deserialization)
     * @see InventoryPage#setPageEventManager(PageEventManager)
     */
    public @NotNull PageEventManager onInteract(@NotNull Consumer<InventoryInteractEvent> consumer) {
        Objects.requireNonNull(consumer);
        interactEvents.add(consumer);
        return this;
    }

    /**
     * @param consumer A consumer to handle the {@link InventoryMoveItemEvent}
     * @return An instance of this
     * @apiNote EventHandler are not possible to serialize (Add the listeners on deserialization)
     * @see InventoryPage#setPageEventManager(PageEventManager)
     */
    public @NotNull PageEventManager onMoveItem(@NotNull Consumer<InventoryMoveItemEvent> consumer) {
        Objects.requireNonNull(consumer);
        moveItemEvents.add(consumer);
        return this;
    }

    /**
     * Call an event which will send to all consumers
     * @param event The event
     */
    public void callEvent(@NotNull Event event) {
        if(cancelling && event instanceof Cancellable c && (!(event instanceof InventoryOpenEvent || event instanceof InventoryPageChangeEvent || event instanceof InventoryButtonClickEvent)))
            c.setCancelled(true);

        if (event instanceof InventoryCloseEvent e) {
            closeEvents.forEach(consumer -> consumer.accept(e));
            return;
        }

        if (event instanceof InventoryClickEvent e) {
            clickEvents.forEach(consumer -> consumer.accept(e));
            return;
        }

        if (event instanceof InventoryOpenEvent e) {
            openEvents.forEach(consumer -> consumer.accept(e));
            return;
        }

        if (event instanceof InventoryPageChangeEvent e) {
            changePageEvents.forEach(consumer -> consumer.accept(e));
            return;
        }

        if (event instanceof InventoryButtonClickEvent e) {
            buttonClickEvents.forEach(consumer -> consumer.accept(e));
            return;
        }

        if(event instanceof InventoryPickupItemEvent e){
            pickupEvents.forEach(consumer -> consumer.accept(e));
            return;
        }

        if(event instanceof InventoryDragEvent e){
            dragEvents.forEach(consumer -> consumer.accept(e));
            return;
        }

        if(event instanceof InventoryInteractEvent e){
            interactEvents.forEach(consumer -> consumer.accept(e));
            return;
        }

        if(event instanceof InventoryMoveItemEvent e){
            moveItemEvents.forEach(consumer -> consumer.accept(e));
        }
    }

    /**
     * Set {@link org.bukkit.event.Cancellable#setCancelled(boolean)} on every event to true
     * @return An instance of this
     */
    public PageEventManager enableCancelling() {
        this.cancelling=true;
        return this;
    }
}
