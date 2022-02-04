package de.robotricker.transportpipes.duct.pipe;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.duct.pipe.filter.ItemDistributorService;
import de.robotricker.transportpipes.duct.pipe.items.PipeItem;
import de.robotricker.transportpipes.duct.types.DuctType;
import de.robotricker.transportpipes.inventory.DuctSettingsInventory;
import de.robotricker.transportpipes.items.ItemService;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.TPDirection;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class IronPipe extends Pipe {

    private TPDirection currentOutputDirection;

    public IronPipe(DuctType ductType, BlockLocation blockLoc, World world, Chunk chunk, DuctSettingsInventory settingsInv, GlobalDuctManager globalDuctManager, ItemDistributorService itemDistributor) {
        super(ductType, blockLoc, world, chunk, settingsInv, globalDuctManager, itemDistributor);
        currentOutputDirection = TPDirection.UP;
    }

    @Override
    public void notifyClick(Player p, boolean shift) {
        super.notifyClick(p, shift);
        cycleOutputDirection();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
    }

    public TPDirection getCurrentOutputDirection() {
        return currentOutputDirection;
    }

    public void setCurrentOutputDirection(TPDirection currentOutputDirection) {
        this.currentOutputDirection = currentOutputDirection;
    }

    @Override
    protected Map<TPDirection, Integer> calculateItemDistribution(PipeItem pipeItem, TPDirection movingDir, List<TPDirection> dirs, TransportPipes transportPipes) {
        Map<TPDirection, Integer> absWeights = new HashMap<>();
        if (dirs.contains(getCurrentOutputDirection())) {
            absWeights.put(getCurrentOutputDirection(), 1);
        }
        return itemDistributor.splitPipeItem(pipeItem, absWeights, this);
    }

    private void cycleOutputDirection() {
        ArrayList<TPDirection> allConnections = new ArrayList<>(getAllConnections());
        if (allConnections.isEmpty()) {
            return;
        }
        
        TPDirection oldOutputDirection = currentOutputDirection;
        
        if (getAllConnections().contains(currentOutputDirection)) {
            Collections.rotate(allConnections, -getAllConnections().headSet(currentOutputDirection).size());
        }
            
        for (TPDirection direction : allConnections) {
            if (direction == currentOutputDirection) {
                continue;
            }
            if (getDuctConnections().containsKey(direction)) {
                Duct duct = getDuctConnections().get(direction);
                if (duct instanceof IronPipe) {
                    if (direction == ((IronPipe) duct).getCurrentOutputDirection().getOpposite()) {
                        continue;
                    }
                }
            }
            currentOutputDirection = direction;
            break;
        }
        
        if (oldOutputDirection != currentOutputDirection) {
            globalDuctManager.updateDuctInRenderSystems(this, true);
        }
    }

    @Override
    public Material getBreakParticleData() {
        return Material.IRON_BLOCK;
    }

    @Override
    public void notifyConnectionChange() {
        super.notifyConnectionChange();
        Set<TPDirection> allConns = getAllConnections();
        if (!allConns.isEmpty() && !allConns.contains(currentOutputDirection)) {
            cycleOutputDirection();
        }
    }

    @Override
    public void saveToNBTTag(CompoundTag compoundTag, ItemService itemService) {
        super.saveToNBTTag(compoundTag, itemService);

        compoundTag.putInt("outputDir", currentOutputDirection.ordinal());

    }

    @Override
    public void loadFromNBTTag(CompoundTag compoundTag, ItemService itemService) {
        super.loadFromNBTTag(compoundTag, itemService);

        currentOutputDirection = TPDirection.values()[compoundTag.getInt("outputDir")];

    }
}
