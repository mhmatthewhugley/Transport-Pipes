package de.robotricker.transportpipes.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ProtocolProvider {

    default int getMaskIndex() {
        return 14;
    }

    default int getHeadRotIndex() {
        return 15;
    }

    default int getRightArmRotIndex() {
        return 18;
    }

    // Armor stand entity destroy packet
    default void removeASD(Player p, List<ArmorStandData> armorStandData, ProtocolManager protocolManager) {
        PacketContainer entityDestroyContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        int[] ids = armorStandData.stream().mapToInt(ArmorStandData::getEntityID).toArray();
        entityDestroyContainer.getIntegerArrays().write(0, ids);
        protocolManager.sendServerPacket(p, entityDestroyContainer);
    }

    // Crafting pipe recipe calculation
    Recipe calculateRecipe(TransportPipes transportPipes, Inventory inventory, Duct duct);

    // Set ASD Yaw
    default StructureModifier setASDYaw(PacketContainer spawnEntityLivingContainer, double yaw) {
        return spawnEntityLivingContainer.getIntegers().write(5, (int) (yaw * 256 / 360));
    }

    List<Class<? extends Directional>> clickedFaceDirectionals = new ArrayList<>(Arrays.asList(Bell.class, Cocoa.class,
            CoralWallFan.class, Grindstone.class, Hopper.class, Ladder.class, RedstoneWallTorch.class, Switch.class,
            TrapDoor.class, TripwireHook.class, WallSign.class));

    List<Material> clickedFaceMaterials = new ArrayList<>(Arrays.asList(Material.BONE_BLOCK, Material.BASALT,
            Material.POLISHED_BASALT, Material.CHAIN, Material.HAY_BLOCK, Material.PURPUR_PILLAR, Material.QUARTZ_PILLAR));

    default boolean isClickedFaceDirectional(BlockData blockData) {
        Material material = blockData.getMaterial();
        return Tag.LOGS.isTagged(material) || Tag.SHULKER_BOXES.isTagged(material)
                || clickedFaceMaterials.stream().anyMatch(clickedFaceMaterial -> material == clickedFaceMaterial)
                || clickedFaceDirectionals.stream().anyMatch(clickedFaceDirectional -> clickedFaceDirectional.isInstance(blockData));
    }

}
