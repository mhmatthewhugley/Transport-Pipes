package de.robotricker.transportpipes.items;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.types.DuctType;

public abstract class ItemManager<T extends Duct> {

    protected final Map<DuctType, ItemStack> items;

    public ItemManager() {
        this.items = new HashMap<>();
    }

    public ItemStack getItem(DuctType ductType) {
        return items.get(ductType);
    }

    public ItemStack getClonedItem(DuctType ductType) {
        return getItem(ductType).clone();
    }

    public abstract void registerItems();

}
