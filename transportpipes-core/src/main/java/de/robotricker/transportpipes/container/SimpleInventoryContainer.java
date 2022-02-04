package de.robotricker.transportpipes.container;

import de.robotricker.transportpipes.api.DuctExtractEvent;
import de.robotricker.transportpipes.api.DuctInsertEvent;
import de.robotricker.transportpipes.duct.pipe.filter.ItemFilter;
import de.robotricker.transportpipes.location.TPDirection;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Objects;

public class SimpleInventoryContainer extends BlockContainer {

    private final Chunk chunk;
    private InventoryHolder cachedInvHolder;
    private Inventory cachedInv;

    public SimpleInventoryContainer(Block block) {
        super(block);
        this.chunk = block.getChunk();
        this.cachedInvHolder = (InventoryHolder) block.getState();
        this.cachedInv = cachedInvHolder.getInventory();
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
        if (isInvLocked(cachedInvHolder)) {
            return null;
        }
        ItemStack itemTaken = null;
        for (int i = 0; i < cachedInv.getSize(); i++) {
            if (itemFilter.applyFilter(cachedInv.getItem(i)).getWeight() > 0) {
                int amountBefore = itemTaken != null ? itemTaken.getAmount() : 0;
                if (itemTaken == null) {
                    itemTaken = Objects.requireNonNull(cachedInv.getItem(i)).clone();
                    itemTaken.setAmount(Math.min(Math.min(amount, itemTaken.getAmount()), itemTaken.getMaxStackSize()));
                } else if (itemTaken.isSimilar(cachedInv.getItem(i))) {
                    itemTaken.setAmount(Math.min(Math.min(amount, amountBefore + Objects.requireNonNull(cachedInv.getItem(i)).getAmount()), itemTaken.getMaxStackSize()));
                }
                ItemStack invItem = Objects.requireNonNull(cachedInv.getItem(i)).clone();
                invItem.setAmount(invItem.getAmount() - (itemTaken.getAmount() - amountBefore));
                DuctExtractEvent event = new DuctExtractEvent(cachedInv, invItem);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()){
                    if(amountBefore > 0) {
                        itemTaken.setAmount(amountBefore);
                    } else itemTaken = null;
                    continue;
                }
                cachedInv.setItem(i, invItem.getAmount() <= 0 ? null : invItem);
            }
        }
        return itemTaken;
    }

    @Override
    public ItemStack insertItem(TPDirection insertDirection, ItemStack insertion) {
        if (!isInLoadedChunk()) {
            return insertion;
        }
        if (isInvLocked(cachedInvHolder)) {
            return insertion;
        }
        DuctInsertEvent insertEvent = new DuctInsertEvent(cachedInv, insertion);
        Bukkit.getServer().getPluginManager().callEvent(insertEvent);
        if(insertEvent.isCancelled()){
            return insertion;
        }
        Collection<ItemStack> overflow = cachedInv.addItem(insertion).values();
        if (overflow.isEmpty()) {
            return null;
        } else {
            return overflow.stream().findFirst().get();
        }
    }

    @Override
    public int spaceForItem(TPDirection insertDirection, ItemStack insertion) {
        if (isInvLocked(cachedInvHolder)) {
            return 0;
        }

        int space = 0;

        for (int i = 0; i < cachedInv.getSize(); i++) {
            ItemStack item = cachedInv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                space += insertion.getMaxStackSize();
            } else if (item.isSimilar(insertion) && item.getAmount() < item.getMaxStackSize()) {
                space += item.getMaxStackSize() - item.getAmount();
            }
        }

        return space;
    }

    @Override
    public void updateBlock() {
        this.cachedInvHolder = ((InventoryHolder) block.getState()).getInventory().getHolder();
        this.cachedInv = Objects.requireNonNull(cachedInvHolder).getInventory();
    }

}
