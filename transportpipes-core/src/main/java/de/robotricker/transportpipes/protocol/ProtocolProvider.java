package de.robotricker.transportpipes.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
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

    WrappedDataWatcher.Serializer BYTE_SERIALIZER = WrappedDataWatcher.Registry.get(Byte.class);
    WrappedDataWatcher.Serializer VECTOR_SERIALIZER = WrappedDataWatcher.Registry.getVectorSerializer();
    WrappedDataWatcher.Serializer BOOLEAN_SERIALIZER = WrappedDataWatcher.Registry.get(Boolean.class);

    default PacketContainer setEntityMetadata(ProtocolManager protocolManager, ArmorStandData asd) {
        PacketContainer entityMetadataContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        entityMetadataContainer.getModifier().writeDefaults();
        entityMetadataContainer.getIntegers().write(0, asd.getEntityID()); // Entity ID

        byte bitMask = (byte) ((asd.isSmall() ? 0x01 : 0x00) | 0x04 | 0x08 | 0x10); // Is Small + Has Arms + No BasePlate + Marker

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject entityMask = new WrappedDataWatcher.WrappedDataWatcherObject(0, BYTE_SERIALIZER);
        WrappedDataWatcher.WrappedDataWatcherObject nameVisible = new WrappedDataWatcher.WrappedDataWatcherObject(3, BOOLEAN_SERIALIZER);
        WrappedDataWatcher.WrappedDataWatcherObject asMask = new WrappedDataWatcher.WrappedDataWatcherObject(getMaskIndex(), BYTE_SERIALIZER);
        WrappedDataWatcher.WrappedDataWatcherObject headRot = new WrappedDataWatcher.WrappedDataWatcherObject(getHeadRotIndex(), VECTOR_SERIALIZER);
        WrappedDataWatcher.WrappedDataWatcherObject rArmRot = new WrappedDataWatcher.WrappedDataWatcherObject(getRightArmRotIndex(), VECTOR_SERIALIZER);

        dataWatcher.setObject(entityMask, (byte) (0x20 | 0x01)); // Invisible and on fire (to fix lighting issues)
        dataWatcher.setObject(nameVisible, false); // Custom Name Visible
        dataWatcher.setObject(asMask, bitMask); // Armor Stand Data
        dataWatcher.setObject(headRot, new Vector3F((float) asd.getHeadRotation().getX(), (float) asd.getHeadRotation().getY(), (float) asd.getHeadRotation().getZ())); // Head Rotation
        dataWatcher.setObject(rArmRot, new Vector3F((float) asd.getArmRotation().getX(), (float) asd.getArmRotation().getY(), (float) asd.getArmRotation().getZ())); // Right Arm Rotation
        entityMetadataContainer.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        return entityMetadataContainer;
    }

}
