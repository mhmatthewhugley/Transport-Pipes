package de.robotricker.transportpipes.utils.ProtectionUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class FakeBlock {
    public abstract Block getBlock(World world, Location location, Material material);
}
