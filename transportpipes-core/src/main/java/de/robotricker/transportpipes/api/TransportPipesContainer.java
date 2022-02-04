package de.robotricker.transportpipes.api;

import de.robotricker.transportpipes.duct.pipe.filter.ItemFilter;
import de.robotricker.transportpipes.location.TPDirection;
import org.bukkit.inventory.ItemStack;

public interface TransportPipesContainer {

    /**
     * Extract items from a TransportPipesContainer
     * @param extractDirection The TPDirection to extract from
     * @param amount The amount of items to extract
     * @param itemFilter The ItemFilter to be applied
     * @return The ItemStack extracted within the given amount if possible. If there are not enough items left to extract, returns what's available.
     */
    ItemStack extractItem(TPDirection extractDirection, int amount, ItemFilter itemFilter);

    /**
     * Insert items into a Container
     * @param insertDirection The TPDirection to insert into
     * @param insertion The ItemStack to insert
     * @return The ItemStack inserted with the amount that did not fit inside the container or Null if all fit inside.
     */
    ItemStack insertItem(TPDirection insertDirection, ItemStack insertion);

    /**
     * Get the amount of space available in a container for an ItemStack. This method is called asynchronously.
     * @param insertDirection The TPDirection to check
     * @param insertion The ItemStack to check
     * @return The ItemStack to check with the amount that would fit into the container
     */
    int spaceForItem(TPDirection insertDirection, ItemStack insertion);

    /**
     * Gets if this TransportPipesContainer is in a loaded chunk
     * @return True if the Container is in a loaded chunk, False if the Container is not in a loaded chunk
     */
    boolean isInLoadedChunk();

}
