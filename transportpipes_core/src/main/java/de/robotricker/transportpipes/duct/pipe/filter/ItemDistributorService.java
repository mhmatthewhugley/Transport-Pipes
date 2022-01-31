package de.robotricker.transportpipes.duct.pipe.filter;

import de.robotricker.transportpipes.api.TransportPipesContainer;
import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.DuctRegister;
import de.robotricker.transportpipes.duct.manager.DuctManager;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.duct.manager.PipeManager;
import de.robotricker.transportpipes.duct.pipe.CraftingPipe;
import de.robotricker.transportpipes.duct.pipe.Pipe;
import de.robotricker.transportpipes.duct.pipe.extractionpipe.ExtractMode;
import de.robotricker.transportpipes.duct.pipe.items.PipeItem;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.TPDirection;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

public class ItemDistributorService {

    @Inject
    private DuctRegister ductRegister;
    @Inject
    private GlobalDuctManager globalDuctManager;

    private final Map<Pipe, Integer> pipeItemDistributionCounter;

    public ItemDistributorService() {
        this.pipeItemDistributionCounter = new HashMap<>();
    }

    /**
     * calculates the greatest common dividor of all abs weights and divides every weight by it.
     **/
    private void reduceAbsWeights(Map<TPDirection, Integer> absWeights) {
        BigInteger gcd = null;
        for (TPDirection dir : absWeights.keySet()) {
            if (absWeights.get(dir) == 0) {
                continue;
            }
            if (gcd == null) {
                gcd = BigInteger.valueOf(absWeights.get(dir));
            } else {
                gcd = BigInteger.valueOf(absWeights.get(dir)).gcd(gcd);
            }
        }
        for (TPDirection dir : absWeights.keySet()) {
            if (absWeights.get(dir) == 0) {
                continue;
            }
            absWeights.put(dir, absWeights.get(dir) / Objects.requireNonNull(gcd).intValue());
        }
    }

    /**
     * this method maps the possible directions with their free space
     */
    private Map<TPDirection, Integer> calculateFreeSpaceForAllDirections(ItemStack item, Collection<TPDirection> dirs, Pipe pipe) {
        Map<TPDirection, Integer> freeSpaceMap = new HashMap<>();

        Map<BlockLocation, TransportPipesContainer> containerMap = ((PipeManager) (DuctManager<? extends Duct>) ductRegister.baseDuctTypeOf("pipe").getDuctManager()).getContainers(pipe.getWorld());
        Map<BlockLocation, Duct> ductMap = globalDuctManager.getDucts(pipe.getWorld());

        for (TPDirection dir : dirs) {
            freeSpaceMap.put(dir, Integer.MAX_VALUE);
            BlockLocation bl = pipe.getBlockLoc().getNeighbor(dir);
            if (containerMap != null && containerMap.containsKey(bl)) {
                // container at location
                TransportPipesContainer container = containerMap.get(bl);
                int freeSpace = container.spaceForItem(dir, item);
                freeSpaceMap.put(dir, freeSpace);
            } else if (ductMap != null && ductMap.containsKey(bl) && ductMap.get(bl) instanceof CraftingPipe cp) {
                // crafting pipe at location
                int freeSpace = cp.spaceForItem(new ItemData(item));
                freeSpaceMap.put(dir, freeSpace);
            }
        }

        return freeSpaceMap;
    }
    public Map<TPDirection, Integer> splitPipeItem(PipeItem pipeItem, Map<TPDirection, Integer> absWeights, Pipe pipe) {
        return splitPipeItem(pipeItem, absWeights, pipe, null);
    }

    public Map<TPDirection, Integer> splitPipeItem(PipeItem pipeItem, Map<TPDirection, Integer> absWeights, Pipe pipe, Map<TPDirection, Integer> origWeights) {
        ItemStack item = pipeItem.getItem();
        Map<TPDirection, Integer> splitMap = new HashMap<>();
        Map<TPDirection, Integer> freeSpaceMap = calculateFreeSpaceForAllDirections(item, absWeights.keySet(), pipe);
        
        reduceAbsWeights(absWeights);

        List<TPDirection> weightsDirectionList = new ArrayList<>();
        for (TPDirection dir : absWeights.keySet()) {
            int absWeight = absWeights.get(dir);
            for (int i = 1; i <= absWeight; i++) {
                // make sure the free space of the nearby container block does not get ignored
                if (freeSpaceMap.get(dir) >= i) {
                    weightsDirectionList.add(dir);
                }
            }
        }

        if (weightsDirectionList.isEmpty()) {
            // return empty map so the item will be dropped
            // if null would be returned, no item will be dropped
            if (origWeights != null) {
                return splitPipeItem(pipeItem, origWeights, pipe, null);
            }
            else {
                return splitMap;
            }
        }

        int distributionCounter = pipeItemDistributionCounter.getOrDefault(pipe, 0);

        if (pipeItem.getExtractMode() == ExtractMode.DIRECT) {
            splitMap.put(weightsDirectionList.get(0), item.getAmount());
        }
        else {
            for (int i = 0; i < item.getAmount(); i++) {
                distributionCounter %= weightsDirectionList.size();
    
                TPDirection outputDir = weightsDirectionList.get(distributionCounter);
                if (splitMap.containsKey(outputDir)) {
                    splitMap.put(outputDir, splitMap.get(outputDir) + 1);
                } else {
                    splitMap.put(outputDir, 1);
                }
    
                distributionCounter++;
            }
        }

        if (!pipeItem.getVisitedPipes().contains(pipe.getBlockLoc())) {
            pipeItemDistributionCounter.put(pipe, distributionCounter);
            pipeItem.addVisitedPipe(pipe.getBlockLoc());
        }

        return splitMap;
    }

}
