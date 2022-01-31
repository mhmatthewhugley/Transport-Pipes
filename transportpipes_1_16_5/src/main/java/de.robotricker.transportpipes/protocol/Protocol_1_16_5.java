package de.robotricker.transportpipes.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

public class Protocol_1_16_5 extends ProtocolProvider {

    public Protocol_1_16_5() {
        super(14, 15, 18);
    }

    @Override
    public void removeASD(Player p, List<ArmorStandData> armorStandData, ProtocolManager protocolManager) {
        PacketContainer entityDestroyContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        int[] ids = armorStandData.stream().mapToInt(ArmorStandData::getEntityID).toArray();
        entityDestroyContainer.getIntegerArrays().write(0, ids);
        try {
            protocolManager.sendServerPacket(p, entityDestroyContainer);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

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
        Optional<RecipeCrafting> recipeCrafting = world.getMinecraftServer().getCraftingManager().craft(Recipes.CRAFTING, inventoryCrafting, world);

        return recipeCrafting.map(IRecipe::toBukkitRecipe).orElse(null);
    }
}
