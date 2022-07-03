package de.robotricker.transportpipes.protocol;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Optional;

public abstract class Protocol_1_16_5 implements ProtocolProvider {

    @Override
    public Recipe calculateRecipe(TransportPipes transportPipes, Inventory inventory, Duct duct) {
        ItemStack[] craftingMatrix = new ItemStack[9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (inventory.getItem(10 + row * 9 + col) != null) {
                    craftingMatrix[row * 3 + col] = inventory.getItem(10 + row * 9 + col);
                } else {
                    craftingMatrix[row * 3 + col] = new ItemStack(Material.AIR);
                }
            }
        }

        Container container = new Container(null, -1) {
            @Override
            public InventoryView getBukkitView() {
                return null;
            }

            @Override
            public boolean canUse(EntityHuman entityHuman) {
                return false;
            }
        };

        InventoryCrafting inventoryCrafting = new InventoryCrafting(container, 3, 3);
        for (int i = 0; i < craftingMatrix.length; i++) {
            inventoryCrafting.setItem(i, CraftItemStack.asNMSCopy(craftingMatrix[i]));
        }

        World world = ((CraftWorld) duct.getWorld()).getHandle();
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) return null;
        Optional<RecipeCrafting> recipeCrafting = server.getCraftingManager().craft(Recipes.CRAFTING, inventoryCrafting, world);

        return recipeCrafting.map(IRecipe::toBukkitRecipe).orElse(null);
    }
}
