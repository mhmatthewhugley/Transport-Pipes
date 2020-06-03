package de.robotricker.transportpipes.duct.pipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.duct.pipe.filter.ItemDistributorService;
import de.robotricker.transportpipes.duct.pipe.items.PipeItem;
import de.robotricker.transportpipes.duct.types.DuctType;
import de.robotricker.transportpipes.inventory.DuctSettingsInventory;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.TPDirection;

public class IcePipe extends Pipe {

    public IcePipe(DuctType ductType, BlockLocation blockLoc, World world, Chunk chunk, DuctSettingsInventory settingsInv, GlobalDuctManager globalDuctManager, ItemDistributorService itemDistributor) {
        super(ductType, blockLoc, world, chunk, settingsInv, globalDuctManager, itemDistributor);
    }

    @Override
    double getPipeItemSpeed() {
        return super.getPipeItemSpeed() * 4;
    }

    @Override
    public Material getBreakParticleData() {
        return Material.ICE;
    }
    
    @Override
    protected Map<TPDirection, Integer> calculateItemDistribution(PipeItem pipeItem, TPDirection movingDir, List<TPDirection> dirs, TransportPipes transportPipes) {
        BlockLocation location = getBlockLoc();
        List<TPDirection> newDirs = dirs.stream().filter(dir -> pipeItem.hasMovedDirs(location) && !pipeItem.getMovedDirs(location).contains(dir)).collect(Collectors.toList());
        if (newDirs.isEmpty()) {
            newDirs = dirs;
        }
        
        // If we have more than one direction option, make sure we remove the opposite direction to prevent backtracking when possible
        if (newDirs.contains(movingDir.getOpposite()) && newDirs.size() > 1) {
            newDirs.remove(movingDir.getOpposite());
        }
        
        Map<TPDirection, Integer> absWeights = new HashMap<>();
        newDirs.stream().forEach(dir -> absWeights.put(dir, 1));
        return itemDistributor.splitPipeItem(pipeItem.getItem(), absWeights, this);
	}

}
