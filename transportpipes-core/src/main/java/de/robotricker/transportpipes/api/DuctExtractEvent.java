package de.robotricker.transportpipes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DuctExtractEvent extends Event implements Cancellable {

	private final Inventory source;
	private final ItemStack item;
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	/** Called when a duct tries to extract an ItemStack from a container
	 *  If this event is cancelled, the ItemStack will not be extracted from the container
	 *
	 * @param source The inventory that the items is to be removed from
	 * @param item  The ItemStack that is to be removed
	 */
	public DuctExtractEvent(Inventory source, ItemStack item) {
		super();
		this.source = source;
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

	/** Get the inventory the item is to be removed from
	 *
	 * @return The source inventory
	 */
	public Inventory getSource() {
		return source;
	}

	/** Get the ItemStack that is to be removed
	 *
	 * @return The ItemStack to be removed
	 */
	public ItemStack getItem() {
		return item;
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
