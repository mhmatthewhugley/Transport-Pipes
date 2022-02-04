package de.robotricker.transportpipes.duct.pipe;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.duct.pipe.filter.ItemDistributorService;
import de.robotricker.transportpipes.duct.pipe.items.PipeItem;
import de.robotricker.transportpipes.duct.types.DuctType;
import de.robotricker.transportpipes.duct.types.pipetype.ColoredPipeType;
import de.robotricker.transportpipes.inventory.DuctSettingsInventory;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.TPDirection;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ColoredPipe extends Pipe {

    public ColoredPipe(DuctType ductType, BlockLocation blockLoc, World world, Chunk chunk, DuctSettingsInventory settingsInv, GlobalDuctManager globalDuctManager, ItemDistributorService itemDistributor) {
        super(ductType, blockLoc, world, chunk, settingsInv, globalDuctManager, itemDistributor);
    }

    @Override
    public Material getBreakParticleData() {
        return ((ColoredPipeType) getDuctType()).getColoringMaterial();
    }
    
    @Override
    protected Map<TPDirection, Integer> calculateItemDistribution(PipeItem pipeItem, TPDirection movingDir, List<TPDirection> dirs, TransportPipes transportPipes) {
    	BlockLocation location = getBlockLoc();
    	TreeSet<TPDirection> newDirs = dirs.stream().filter(dir -> pipeItem.hasMovedDirs(location) && !pipeItem.getMovedDirs(location).contains(dir)).collect(Collectors.toCollection(TreeSet::new));
    	if (newDirs.isEmpty()) {
    		newDirs = new TreeSet<>(dirs);
    	}
        
        // If we have more than one direction option, make sure we remove the opposite direction to prevent backtracking when possible
        if (newDirs.contains(movingDir.getOpposite()) && newDirs.size() > 1) {
            newDirs.remove(movingDir.getOpposite());
        }
        
        TreeMap<TPDirection, Integer> absWeights = new TreeMap<>();
        TreeMap<TPDirection, Integer> origWeights = new TreeMap<>();
        newDirs.forEach(dir -> absWeights.put(dir, 1));
        dirs.forEach(dir -> {
            if (dir != movingDir.getOpposite()) origWeights.put(dir, 1);
        });

        return itemDistributor.splitPipeItem(pipeItem, absWeights, this, origWeights);
	}

}
