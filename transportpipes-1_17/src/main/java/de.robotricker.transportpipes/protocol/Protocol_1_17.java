package de.robotricker.transportpipes.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeCrafting;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.World;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.AmethystCluster;
import org.bukkit.block.data.type.LightningRod;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Protocol_1_17 implements ProtocolProvider {

    @Override
    public int getMaskIndex() {
        return 15;
    }

    @Override
    public int getHeadRotIndex() {
        return 16;
    }

    @Override
    public int getRightArmRotIndex() {
        return 19;
    }

    @Override
    public void removeASD(Player p, List<ArmorStandData> armorStandData, ProtocolManager protocolManager) {
        PacketContainer entityDestroyContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        List<Integer> ids = armorStandData.stream().mapToInt(ArmorStandData::getEntityID).boxed().collect(Collectors.toList());
        entityDestroyContainer.getIntLists().write(0, ids);
        protocolManager.sendServerPacket(p, entityDestroyContainer);
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
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) return null;
        Optional<RecipeCrafting> recipeCrafting = server.getCraftingManager().craft(Recipes.a, inventoryCrafting, world);

        return recipeCrafting.map(IRecipe::toBukkitRecipe).orElse(null);
    }

    @Override
    public boolean isClickedFaceDirectional(BlockData blockData) {
        clickedFaceDirectionals.add(AmethystCluster.class);
        clickedFaceDirectionals.add(LightningRod.class);
        clickedFaceMaterials.add(Material.DEEPSLATE);
        clickedFaceMaterials.add(Material.INFESTED_DEEPSLATE);
        return ProtocolProvider.super.isClickedFaceDirectional(blockData);
    }
}
