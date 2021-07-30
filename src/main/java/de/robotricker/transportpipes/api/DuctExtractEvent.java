package de.robotricker.transportpipes.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DuctExtractEvent extends Event {

	private final Inventory source;
	private final ItemStack item;
	private static final HandlerList handlers = new HandlerList();

	public DuctExtractEvent(Inventory source, ItemStack item) {
		super();
		this.source = source;
		this.item = item;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Inventory getSource() {
		return source;
	}

	public ItemStack getItem() {
		return item;
	}
}
