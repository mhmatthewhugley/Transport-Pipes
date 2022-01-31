package de.robotricker.transportpipes.utils.ProtectionUtils;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BreakPermissionEvent extends BlockBreakEvent {

    private boolean allowed = true;

    public BreakPermissionEvent(@NotNull Block theBlock, @NotNull Player player) {
        super(theBlock, player);
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public boolean isAllowed() {
        return allowed;
    }
}