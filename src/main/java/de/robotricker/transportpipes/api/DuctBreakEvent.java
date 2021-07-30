package de.robotricker.transportpipes.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DuctBreakEvent extends Event {

    final Player player;
    private static final HandlerList handlers = new HandlerList();

    public DuctBreakEvent(Player player) {
        super();
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public Player getPlayer() {
        return player;
    }
}