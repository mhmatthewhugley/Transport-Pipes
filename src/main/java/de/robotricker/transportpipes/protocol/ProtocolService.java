package de.robotricker.transportpipes.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.pipe.items.PipeItem;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.RelativeLocation;
import de.robotricker.transportpipes.utils.NMSUtils;

public class ProtocolService {

    private WrappedDataWatcher.Serializer BYTE_SERIALIZER;
    private WrappedDataWatcher.Serializer VECTOR_SERIALIZER;
    private WrappedDataWatcher.Serializer BOOLEAN_SERIALIZER;
    
    private ProtocolManager protocolManager;
    
    private TransportPipes plugin;

    @Inject
    public ProtocolService(TransportPipes plugin) {
        BYTE_SERIALIZER = WrappedDataWatcher.Registry.get(Byte.class);
        VECTOR_SERIALIZER = WrappedDataWatcher.Registry.get(NMSUtils.getVector3fClass());
        BOOLEAN_SERIALIZER = WrappedDataWatcher.Registry.get(Boolean.class);
        
        protocolManager = ProtocolLibrary.getProtocolManager();
        
        this.plugin = plugin;
    }

    private int nextEntityID = 99999;
    private UUID uuid = UUID.randomUUID();

    public void sendPipeItem(Player p, PipeItem item) {
        sendASD(p, item.getBlockLoc(), item.getRelativeLocation().clone().add(-0.5d, -0.5d, -0.5d), item.getAsd());
    }

    public void updatePipeItem(Player p, PipeItem item) {
        try {
        	PacketContainer relEntityMoveContainer = protocolManager.createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
        	relEntityMoveContainer.getIntegers().write(0, item.getAsd().getEntityID());
        	relEntityMoveContainer.getShorts().write(0, (short) ((item.getRelativeLocationDifference().getDoubleX() * 32d) * 128));
        	relEntityMoveContainer.getShorts().write(1, (short) ((item.getRelativeLocationDifference().getDoubleY() * 32d) * 128));
        	relEntityMoveContainer.getShorts().write(2, (short) ((item.getRelativeLocationDifference().getDoubleZ() * 32d) * 128));
        	relEntityMoveContainer.getBooleans().write(0, true);
        	protocolManager.sendServerPacket(p, relEntityMoveContainer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePipeItem(final Player p, PipeItem item) {
        removeASD(p, Collections.singletonList(item.getAsd()));
    }

    public void sendASD(Player p, BlockLocation blockLoc, RelativeLocation offset, ArmorStandData asd) {
        int serverVersion = NMSUtils.gatherProtocolVersion();

        try {
            if (asd.getEntityID() == -1) {
                asd.setEntityID(++nextEntityID);
            }
            
            // SPAWN ENTITY
            double x = asd.getDirection().getX();
            double z = asd.getDirection().getZ();

            double theta = Math.atan2(-x, z);
            double yaw = Math.toDegrees(theta);
            
            PacketContainer spawnEntityLivingContainer = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
            spawnEntityLivingContainer.getIntegers().write(0, asd.getEntityID()); // Entity ID
            spawnEntityLivingContainer.getIntegers().write(1, 0); // Optional Velocity X
            spawnEntityLivingContainer.getIntegers().write(2, 0); // Optional Velocity Y
            spawnEntityLivingContainer.getIntegers().write(3, 0); // Optional Velocity Z
            spawnEntityLivingContainer.getIntegers().write(4, 0); // Pitch
            spawnEntityLivingContainer.getIntegers().write(5, (int)(yaw * 256.0F / 360.0F)); // Data (No velocity)
            spawnEntityLivingContainer.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            spawnEntityLivingContainer.getDoubles().write(0, blockLoc.getX() + asd.getRelLoc().getDoubleX() + offset.getDoubleX()); // X Location
            spawnEntityLivingContainer.getDoubles().write(1, blockLoc.getY() + asd.getRelLoc().getDoubleY() + offset.getDoubleY()); // Y Location
            spawnEntityLivingContainer.getDoubles().write(2, blockLoc.getZ() + asd.getRelLoc().getDoubleZ() + offset.getDoubleZ()); // Z Location
            spawnEntityLivingContainer.getUUIDs().write(0, uuid); // Entity UUID
            
            protocolManager.sendServerPacket(p, spawnEntityLivingContainer);

            // ENTITYMETADATA
            
            PacketContainer entityMetadataContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            entityMetadataContainer.getIntegers().write(0, asd.getEntityID()); // Entity ID

            byte bitMask = (byte) ((asd.isSmall() ? 0x01 : 0x00) | 0x04 | 0x08 | 0x10); // Is Small + Has Arms + No BasePlate + Marker

            List<WrappedWatchableObject> metaList = new ArrayList<>();
            metaList.add(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, BYTE_SERIALIZER), (byte) (0x20))); // Invisible
            metaList.add(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, BOOLEAN_SERIALIZER), false)); // Custom Name Visible
            metaList.add(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(serverVersion <= 404 ? 11 : 14, BYTE_SERIALIZER), bitMask)); // Armor Stand Data
            metaList.add(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(serverVersion <= 404 ? 12 : 15, VECTOR_SERIALIZER), NMSUtils.createVector3f((float) asd.getHeadRotation().getX(), (float) asd.getHeadRotation().getY(), (float) asd.getHeadRotation().getZ()))); // Head Rotation
            metaList.add(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(serverVersion <= 404 ? 15 : 18, VECTOR_SERIALIZER), NMSUtils.createVector3f((float) asd.getArmRotation().getX(), (float) asd.getArmRotation().getY(), (float) asd.getArmRotation().getZ()))); // Right Arm Rotation

            entityMetadataContainer.getWatchableCollectionModifier().write(0, metaList);
            protocolManager.sendServerPacket(p, entityMetadataContainer);


            // ENTITYEQUIPMENT
            PacketContainer entityEquipmentContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            entityEquipmentContainer.getIntegers().write(0, asd.getEntityID()); // Entity ID
            // Set Hand Item
            if (asd.getHandItem() != null) {
            	entityEquipmentContainer.getItemSlots().write(0, EnumWrappers.ItemSlot.MAINHAND);
            	entityEquipmentContainer.getItemModifier().write(0, asd.getHandItem());
            }
            // Set Head Item
            if (asd.getHeadItem() != null) {
            	entityEquipmentContainer.getItemSlots().write(0, EnumWrappers.ItemSlot.HEAD);
            	entityEquipmentContainer.getItemModifier().write(0, asd.getHeadItem());
            }
            
            protocolManager.sendServerPacket(p, entityEquipmentContainer);

            // ENTITYMETADATA 2 (fire)
            PacketContainer entityMetadata2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            entityMetadata2.getIntegers().write(0, asd.getEntityID());
            List<WrappedWatchableObject> meta2List = new ArrayList<>();
            meta2List.add(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, BYTE_SERIALIZER), (byte) (0x01 | 0x20))); // On Fire to fix lighting issues
            entityMetadata2.getWatchableCollectionModifier().write(0, meta2List);

            plugin.runTaskAsync(() -> {
                try {
                    protocolManager.sendServerPacket(p, entityMetadata2);
                    protocolManager.sendServerPacket(p, entityEquipmentContainer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 1);

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
        int[] ids = armorStandData.stream().mapToInt(ArmorStandData::getEntityID).toArray();
        entityDestroyContainer.getIntegerArrays().write(0, ids);
        try {
			protocolManager.sendServerPacket(p, entityDestroyContainer);
		}
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
