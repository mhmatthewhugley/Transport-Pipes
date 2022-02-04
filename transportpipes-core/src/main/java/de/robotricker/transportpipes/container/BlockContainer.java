package de.robotricker.transportpipes.container;

import de.robotricker.transportpipes.api.TransportPipesContainer;
import org.bukkit.block.Block;
import org.bukkit.block.Lockable;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class BlockContainer implements TransportPipesContainer {

    protected final Block block;

    public BlockContainer(Block block) {
        this.block = block;
    }

    /**
     * puts "put" into "before" and returns the result. if "put" can't be accumulated with "before", "before" is returned.
     * if "put" does not fit completely in "before", "put"'s amount decreases by how much does fit and the returned item will have an amount of the max stack size
     */
    protected ItemStack accumulateItems(ItemStack before, ItemStack put) {
        if (put == null) {
            return before;
        }
        if (before == null) {
            ItemStack putCopy = put.clone();
            put.setAmount(0);
            return putCopy;
        }
        if (!before.isSimilar(put)) {
            return before;
        }
        int beforeAmount = before.getAmount();
        int putAmount = put.getAmount();
        int maxStackSize = before.getMaxStackSize();
        ItemStack returnItem = before.clone();
        returnItem.setAmount(Math.min(beforeAmount + putAmount, maxStackSize));
        put.setAmount(Math.max(putAmount - (maxStackSize - beforeAmount), 0));
        return returnItem;
    }

    /**
     * does the same as accumulateItems but instead of accumulating them, this method only calculates how much of "put" does fit into "before".
     * The actual amount of "put" is not be taken into account.
     * This method does not change any given ItemStacks
     */
    protected int spaceForItem(ItemStack before, ItemStack put) {
        if (put == null) {
            return 0;
        }
        if (before == null) {
            return put.getMaxStackSize();
        }
        if (!before.isSimilar(put)) {
            return 0;
        }
        if (before.getAmount() >= before.getMaxStackSize()) {
            return 0;
        }
        return before.getMaxStackSize() - before.getAmount();
    }

    protected boolean isInvLocked(InventoryHolder ih) {
        try {
            // check vanilla lock
            if (ih instanceof Lockable) {
                if (((Lockable) ih).isLocked()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public abstract void updateBlock();

}
