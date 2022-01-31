package de.robotricker.transportpipes.duct.pipe.extractionpipe;

import de.robotricker.transportpipes.config.LangConf;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ExtractMode {

    ROUND(LangConf.Key.EXTRACT_MODE_ROUND.get(), Material.COMPARATOR),
    DIRECT(LangConf.Key.EXTRACT_MODE_DIRECT.get(), Material.REPEATER);

    private final String displayName;
    private final ItemStack displayItem;

    ExtractMode(String displayName, Material type) {
        this.displayName = displayName;
        this.displayItem = new ItemStack(type);
    }

    public String getDisplayName() {
        return displayName;
    }

    public ExtractMode next() {
        int ordinal = ordinal();
        ordinal++;
        ordinal %= values().length;
        return values()[ordinal];
    }

    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }

}