package de.robotricker.transportpipes.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DuctInsertEvent extends Event {

	private Inventory destination;
	private ItemStack item;
	private static final HandlerList handlers = new HandlerList();

	public DuctInsertEvent(Inventory destination, ItemStack item) {
		super();
		this.destination = destination;
		this.item = item;
	}

	@Override
	public HandlerList getHandlers() {
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
