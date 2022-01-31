package de.robotricker.transportpipes.duct.factory;

import de.robotricker.transportpipes.duct.pipe.*;
import de.robotricker.transportpipes.duct.pipe.filter.ItemDistributorService;
import de.robotricker.transportpipes.duct.types.DuctType;
import de.robotricker.transportpipes.duct.types.pipetype.ColoredPipeType;
import de.robotricker.transportpipes.inventory.CraftingPipeSettingsInventory;
import de.robotricker.transportpipes.inventory.ExtractionPipeSettingsInventory;
import de.robotricker.transportpipes.inventory.GoldenPipeSettingsInventory;
import de.robotricker.transportpipes.location.BlockLocation;
import org.bukkit.Chunk;
import org.bukkit.World;

import javax.inject.Inject;

public class PipeFactory extends DuctFactory<Pipe> {

    @Inject
    private ItemDistributorService itemDistributor;

    @Override
    public Pipe createDuct(DuctType ductType, BlockLocation blockLoc, World world, Chunk chunk) {
        Pipe pipe = null;
        if (ductType instanceof ColoredPipeType) {
            pipe = new ColoredPipe(ductType, blockLoc, world, chunk, null, globalDuctManager, itemDistributor);
        } else if (ductType.is("Golden")) {
            GoldenPipeSettingsInventory inv = transportPipes.getInjector().newInstance(GoldenPipeSettingsInventory.class);
            pipe = new GoldenPipe(ductType, blockLoc, world, chunk, inv, globalDuctManager, itemDistributor);
            pipe.initSettingsInv(transportPipes);
        } else if (ductType.is("Iron")) {
            pipe = new IronPipe(ductType, blockLoc, world, chunk, null, globalDuctManager, itemDistributor);
        } else if (ductType.is("Ice")) {
            pipe = new IcePipe(ductType, blockLoc, world, chunk, null, globalDuctManager, itemDistributor);
        } else if (ductType.is("Void")) {
            pipe = new VoidPipe(ductType, blockLoc, world, chunk, null, globalDuctManager, itemDistributor);
        } else if (ductType.is("Extraction")) {
            ExtractionPipeSettingsInventory inv = transportPipes.getInjector().newInstance(ExtractionPipeSettingsInventory.class);
            pipe = new ExtractionPipe(ductType, blockLoc, world, chunk, inv, globalDuctManager, itemDistributor);
            pipe.initSettingsInv(transportPipes);
        } else if (ductType.is("Crafting")) {
            CraftingPipeSettingsInventory inv = transportPipes.getInjector().newInstance(CraftingPipeSettingsInventory.class);
            pipe = new CraftingPipe(ductType, blockLoc, world, chunk, inv, globalDuctManager, itemDistributor);
            pipe.initSettingsInv(transportPipes);
        }
        return pipe;
    }

}
