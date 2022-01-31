package de.robotricker.transportpipes.container;

import de.robotricker.transportpipes.duct.pipe.filter.ItemFilter;
import de.robotricker.transportpipes.items.ItemService;
import de.robotricker.transportpipes.location.TPDirection;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class FurnaceContainer extends BlockContainer {

    private final Chunk chunk;
    private Furnace cachedFurnace;
    private FurnaceInventory cachedInv;
    private BlockState cachedBlockState;

    public FurnaceContainer(Block block) {
        super(block);
        this.chunk = block.getChunk();
        this.cachedFurnace = (Furnace) block.getState();
        this.cachedInv = cachedFurnace.getInventory();
        this.cachedBlockState = block.getState();
    }

    @Override
    public boolean isInLoadedChunk() {
        return chunk.isLoaded();
    }

    @Override
    public ItemStack extractItem(TPDirection extractDirection, int amount, ItemFilter itemFilter) {
        if (!isInLoadedChunk()) {
            return null;
        }
        if (isInvLocked(cachedFurnace)) {
            return null;
        }
        if (itemFilter.applyFilter(cachedInv.getResult()).getWeight() > 0) {
            ItemStack resultItem = Objects.requireNonNull(cachedInv.getResult()).clone();
            ItemStack returnItem = resultItem.clone();

            int resultItemAmount = resultItem.getAmount();
            resultItem.setAmount(Math.max(resultItemAmount - amount, 0));
            cachedInv.setResult(resultItem.getAmount() >= 1 ? resultItem : null);

            returnItem.setAmount(Math.min(amount, resultItemAmount));

            return returnItem;
        }
        return null;
    }

    @Override
    public ItemStack insertItem(TPDirection insertDirection, ItemStack insertion) {
        if (!isInLoadedChunk()) {
            return insertion;
        }
        if (isInvLocked(cachedFurnace)) {
            return insertion;
        }
        if (insertDirection == TPDirection.DOWN) {
            if (ItemService.isFurnaceBurnableItem(cachedBlockState, insertion)) {
                ItemStack oldSmelting = cachedInv.getSmelting();
                cachedInv.setSmelting(accumulateItems(oldSmelting, insertion));
                if (insertion == null || insertion.getAmount() == 0) {
                    insertion = null;
                }
            }
        } else if (insertDirection == TPDirection.UP) {
            if (ItemService.isFurnaceFuelItem(insertion)) {
                ItemStack oldFuel = cachedInv.getFuel();
                cachedInv.setFuel(accumulateItems(oldFuel, insertion));
                if (insertion.getAmount() == 0) {
                    insertion = null;
                }
            }
        } else {
            if (ItemService.isFurnaceBurnableItem(cachedBlockState, insertion)) {
                ItemStack oldSmelting = cachedInv.getSmelting();
                cachedInv.setSmelting(accumulateItems(oldSmelting, insertion));
                if (insertion == null || insertion.getAmount() == 0) {
                    insertion = null;
                }
            } else if (ItemService.isFurnaceFuelItem(insertion)) {
                ItemStack oldFuel = cachedInv.getFuel();
                cachedInv.setFuel(accumulateItems(oldFuel, insertion));
                if (insertion.getAmount() == 0) {
                    insertion = null;
                }
            }
        }

        return insertion;
    }

    @Override
    public int spaceForItem(TPDirection insertDirection, ItemStack insertion) {
        if (isInvLocked(cachedFurnace)) {
            return 0;
        }
        if (ItemService.isFurnaceBurnableItem(cachedBlockState, insertion)) {
            if (insertDirection.isSide() || insertDirection == TPDirection.DOWN) {
                return spaceForItem(cachedInv.getSmelting(), insertion);
            } else if (ItemService.isFurnaceFuelItem(insertion)) {
                return spaceForItem(cachedInv.getFuel(), insertion);
            }
        } else if (ItemService.isFurnaceFuelItem(insertion)) {
            return spaceForItem(cachedInv.getFuel(), insertion);
        }
        return 0;
    }

    @Override
    public void updateBlock() {
        this.cachedFurnace = ((Furnace) block.getState());
        this.cachedInv = cachedFurnace.getInventory();
        this.cachedBlockState = block.getState();
    }

}
