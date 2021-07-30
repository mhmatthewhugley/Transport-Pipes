package de.robotricker.transportpipes.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DuctInsertEvent extends Event {

	private final Inventory destination;
	private final ItemStack item;
	private static final HandlerList handlers = new HandlerList();

	public DuctInsertEvent(Inventory destination, ItemStack item) {
		super();
		this.destination = destination;
		this.item = item;
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
}
