package de.robotricker.transportpipes.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeCrafting;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.World;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Protocol_1_17 extends ProtocolProvider {

    public Protocol_1_17() {
        super(15, 16, 19);
    }

    @Override
    public void removeASD(Player p, List<ArmorStandData> armorStandData, ProtocolManager protocolManager) {
        PacketContainer entityDestroyContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        List<Integer> ids = armorStandData.stream().mapToInt(ArmorStandData::getEntityID).boxed().collect(Collectors.toList());
        entityDestroyContainer.getIntLists().write(0, ids);
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
        Optional<RecipeCrafting> recipeCrafting = world.getMinecraftServer().getCraftingManager().craft(Recipes.a, inventoryCrafting, world);

        return recipeCrafting.map(IRecipe::toBukkitRecipe).orElse(null);
    }
}
