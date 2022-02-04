package de.robotricker.transportpipes.duct.factory;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.duct.types.DuctType;
import de.robotricker.transportpipes.location.BlockLocation;
import org.bukkit.Chunk;
import org.bukkit.World;

import javax.inject.Inject;

public abstract class DuctFactory<T extends Duct> {

    @Inject
    protected TransportPipes transportPipes;

    @Inject
    protected GlobalDuctManager globalDuctManager;

    public abstract T createDuct(DuctType ductType, BlockLocation blockLoc, World world, Chunk chunk);

}
