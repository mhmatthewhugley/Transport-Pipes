package de.robotricker.transportpipes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DuctInsertEvent extends Event implements Cancellable{

	private final Inventory destination;
	private ItemStack item;
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	/** Called when a duct tries to insert an ItemStack into a container
	 *  If this event is cancelled, the ItemStack will not be inserted into the container
	 *
	 * @param destination The inventory that the items is to be inserted into
	 * @param item  The ItemStack that is to be inserted
	 */
	public DuctInsertEvent(Inventory destination, ItemStack item) {
		super();
		this.destination = destination;
		this.item = item;
		this.cancelled = false;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/** Get the inventory the item is to be inserted into
	 *
	 * @return The destination inventory
	 */
	public Inventory getDestination() {
		return destination;
	}

	/** Get the ItemStack that is to be inserted
	 *
	 * @return The ItemStack to be inserted
	 */
	public ItemStack getItem() {
		return item;
	}

	/** Set the ItemStack that is to be inserted
	 *
	 * @param item The ItemStack to be inserted
	 */
	public void setItem(ItemStack item) {
		this.item = item;
	}

	/**
	 * Get if this event is cancelled
	 * @return True if cancelled, False if not cancelled
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Set the cancellation status of this event
	 * @param cancelled True to cancel the event, False to not cancel the event
	 */
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
