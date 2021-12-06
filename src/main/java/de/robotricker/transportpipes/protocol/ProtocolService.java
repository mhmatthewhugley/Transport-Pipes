package de.robotricker.transportpipes.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.pipe.items.PipeItem;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.RelativeLocation;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProtocolService {

    private final WrappedDataWatcher.Serializer BYTE_SERIALIZER;
    private final WrappedDataWatcher.Serializer VECTOR_SERIALIZER;
    private final WrappedDataWatcher.Serializer BOOLEAN_SERIALIZER;
    
    private final ProtocolManager protocolManager;
    private final TransportPipes plugin;

    @Inject
    public ProtocolService(TransportPipes plugin) {
        BYTE_SERIALIZER = WrappedDataWatcher.Registry.get(Byte.class);
        VECTOR_SERIALIZER = WrappedDataWatcher.Registry.getVectorSerializer();
        BOOLEAN_SERIALIZER = WrappedDataWatcher.Registry.get(Boolean.class);
        
        protocolManager = ProtocolLibrary.getProtocolManager();
        this.plugin = plugin;
    }

    private int nextEntityID = 99999;

    public void sendPipeItem(Player player, PipeItem item) {
        sendASD(player, item.getBlockLoc(), item.getRelativeLocation().clone().add(-0.5d, -0.5d, -0.5d), item.getAsd());
    }

    public void updatePipeItem(Player player, PipeItem item) {
        try {
        	PacketContainer relEntityMoveContainer = protocolManager.createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
        	relEntityMoveContainer.getIntegers().write(0, item.getAsd().getEntityID());
        	relEntityMoveContainer.getShorts().write(0, (short) ((item.getRelativeLocationDifference().getDoubleX() * 32d) * 128));
        	relEntityMoveContainer.getShorts().write(1, (short) ((item.getRelativeLocationDifference().getDoubleY() * 32d) * 128));
        	relEntityMoveContainer.getShorts().write(2, (short) ((item.getRelativeLocationDifference().getDoubleZ() * 32d) * 128));
        	relEntityMoveContainer.getBooleans().write(0, true);
        	protocolManager.sendServerPacket(player, relEntityMoveContainer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePipeItem(final Player player, PipeItem item) {
        removeASD(player, Collections.singletonList(item.getAsd()));
    }

    public void sendASD(Player player, BlockLocation blockLoc, RelativeLocation offset, ArmorStandData asd) {

        try {
            if (asd.getEntityID() == -1) {
                asd.setEntityID(++nextEntityID);
            }

            UUID uuid = UUID.randomUUID();

            // Calculate yaw
            double x = asd.getDirection().getX();
            double z = asd.getDirection().getZ();
            double theta = Math.atan2(-x, z);
            double yaw = Math.toDegrees(theta);

            // SPAWN ENTITY
            PacketContainer spawnEntityLivingContainer = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
            spawnEntityLivingContainer.getModifier().writeDefaults();
            spawnEntityLivingContainer.getIntegers().write(0, asd.getEntityID()); // Entity ID
            spawnEntityLivingContainer.getUUIDs().write(0, uuid); // Entity UUID
            spawnEntityLivingContainer.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            spawnEntityLivingContainer.getDoubles().write(0, blockLoc.getX() + asd.getRelLoc().getDoubleX() + offset.getDoubleX()); // X Location
            spawnEntityLivingContainer.getDoubles().write(1, blockLoc.getY() + asd.getRelLoc().getDoubleY() + offset.getDoubleY()); // Y Location
            spawnEntityLivingContainer.getDoubles().write(2, blockLoc.getZ() + asd.getRelLoc().getDoubleZ() + offset.getDoubleZ()); // Z Location
            spawnEntityLivingContainer.getIntegers().write(5, (int) (yaw * 256.0f / 360.0f)); // Yaw
            
            protocolManager.sendServerPacket(player, spawnEntityLivingContainer);
            
            PacketContainer entityHeadRotationContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            entityHeadRotationContainer.getIntegers().write(0, asd.getEntityID());
            entityHeadRotationContainer.getBytes().write(0, (byte)(yaw * 256.0f / 360.0f));
            
            protocolManager.sendServerPacket(player, entityHeadRotationContainer);

            // ENTITYMETADATA
            
            PacketContainer entityMetadataContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            entityMetadataContainer.getModifier().writeDefaults();
            entityMetadataContainer.getIntegers().write(0, asd.getEntityID()); // Entity ID

            byte bitMask = (byte) ((asd.isSmall() ? 0x01 : 0x00) | 0x04 | 0x08 | 0x10); // Is Small + Has Arms + No BasePlate + Marker

            WrappedDataWatcher dataWatcher = new WrappedDataWatcher();

            WrappedDataWatcher.WrappedDataWatcherObject entityMask = new WrappedDataWatcher.WrappedDataWatcherObject(0, BYTE_SERIALIZER);
            WrappedDataWatcher.WrappedDataWatcherObject nameVisible = new WrappedDataWatcher.WrappedDataWatcherObject(3, BOOLEAN_SERIALIZER);
            WrappedDataWatcher.WrappedDataWatcherObject asMask = new WrappedDataWatcher.WrappedDataWatcherObject(15, BYTE_SERIALIZER);
            WrappedDataWatcher.WrappedDataWatcherObject headRot = new WrappedDataWatcher.WrappedDataWatcherObject(16, VECTOR_SERIALIZER);
            WrappedDataWatcher.WrappedDataWatcherObject rArmRot = new WrappedDataWatcher.WrappedDataWatcherObject(19, VECTOR_SERIALIZER);
            dataWatcher.setObject(entityMask, (byte) (0x20 | 0x01)); // Invisible and on fire (to fix lighting issues)
            dataWatcher.setObject(nameVisible, false); // Custom Name Visible
            dataWatcher.setObject(asMask, bitMask); // Armor Stand Data
            dataWatcher.setObject(headRot, new Vector3F((float) asd.getHeadRotation().getX(), (float) asd.getHeadRotation().getY(), (float) asd.getHeadRotation().getZ())); // Head Rotation
            dataWatcher.setObject(rArmRot, new Vector3F((float) asd.getArmRotation().getX(), (float) asd.getArmRotation().getY(), (float) asd.getArmRotation().getZ())); // Right Arm Rotation

            entityMetadataContainer.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
            protocolManager.sendServerPacket(player, entityMetadataContainer);


            // ENTITYEQUIPMENT
            PacketContainer entityEquipmentContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            entityEquipmentContainer.getIntegers().write(0, asd.getEntityID()); // Entity ID
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> itemList = new ArrayList<>();
            // Set Hand Item
            if (asd.getHandItem() != null) {
            	itemList.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, asd.getHandItem()));
            }
            // Set Head Item
            if (asd.getHeadItem() != null) {
                itemList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, asd.getHeadItem()));
            }
            // Apply items
            if (!itemList.isEmpty()) {
                entityEquipmentContainer.getSlotStackPairLists().write(0, itemList);
            }

            plugin.runTaskAsync(() -> {
                try {
                    protocolManager.sendServerPacket(player, entityEquipmentContainer);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }, 1L);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendASD(Player p, BlockLocation blockLoc, List<ArmorStandData> armorStandData) {
        for (ArmorStandData asd : armorStandData) {
            sendASD(p, blockLoc, new RelativeLocation(0d, 0d, 0d), asd);
        }
    }

    public void removeASD(Player p, List<ArmorStandData> armorStandData) {
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

}
