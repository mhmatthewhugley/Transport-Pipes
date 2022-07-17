package de.robotricker.transportpipes.utils.ProtectionUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.BlockData;

public abstract class FakeBlock {
    World world;
    Location location;
    Material material;
    BlockData blockData;
    Hopper hopper;
}
