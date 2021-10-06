package de.robotricker.transportpipes.api;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ContainerUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Block block;
    private final boolean add;
    private final boolean updateNeighborPipes;
    private boolean cancelled;

    /**
     * Called when a container is added or removed
     * @param block The container block
     * @param add True if the container is being added, False if the container is being removed
     * @param updateNeighborPipes True to update connected pipes, False to not update connected pipes
     */
    public ContainerUpdateEvent(Block block, boolean add, boolean updateNeighborPipes) {
        super();
        this.block = block;
        this.add = add;
        this.updateNeighborPipes = updateNeighborPipes;
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the container block
     * @return The container block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Gets if the container is being added
     * @return True if the container is being added, False if the conainer is being removed
     */
    public boolean adding() {
        return add;
    }

    /**
     * Gets if connected pipes are being updated
     * @return True if connected pipes are being updated, False if connected pipes are not being updated
     */
    public boolean isUpdateNeighborPipes() {
        return updateNeighborPipes;
    }

    /**
     * Get if this event is cancelled
     * @return True if cancelled, False if not cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Set the cancellation status of this event
     * @param cancelled True to cancel the event, False to not cancel the event
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
