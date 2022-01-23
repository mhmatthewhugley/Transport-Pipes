package de.robotricker.transportpipes.api;

import de.robotricker.transportpipes.location.BlockLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DuctPlaceEvent extends Event {

    final Player player;
    final BlockLocation location;
    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a duct is placed
     * @param player The player that placed the duct
     */
    public DuctPlaceEvent(Player player, BlockLocation location) {
        super();
        this.player = player;
        this.location = location;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the player that placed the duct
     * @return The player that placed the duct
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the location of the duct that was placed
     * @return The location of the duct that was placed
     */
    public BlockLocation getLocation() {
        return location;
    }
}