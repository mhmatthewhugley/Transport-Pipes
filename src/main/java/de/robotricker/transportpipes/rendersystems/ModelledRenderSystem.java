package de.robotricker.transportpipes.rendersystems;

import org.bukkit.inventory.ItemStack;

import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.types.BaseDuctType;
import de.robotricker.transportpipes.items.ItemService;

public abstract class ModelledRenderSystem extends RenderSystem {

    public ModelledRenderSystem(BaseDuctType<? extends Duct> baseDuctType) {
        super(baseDuctType);
    }

    public static ItemStack getItem(ItemService itemService) {
        return itemService.createModelledItem(25);
    }

    @SuppressWarnings("SameReturnValue")
    public static String getDisplayName() {
        return "MODELLED";
    }

}
