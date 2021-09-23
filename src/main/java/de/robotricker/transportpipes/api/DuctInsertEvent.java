package de.robotricker.transportpipes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DuctInsertEvent extends Event implements Cancellable{

	private final Inventory destination;
	private final ItemStack item;
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

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

	public Inventory getDestination() {
		return destination;
	}

	public ItemStack getItem() {
		return item;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}

}
