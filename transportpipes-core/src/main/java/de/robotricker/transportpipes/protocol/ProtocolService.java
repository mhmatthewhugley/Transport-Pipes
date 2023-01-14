package de.robotricker.transportpipes.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.pipe.items.PipeItem;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.RelativeLocation;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ProtocolService {
    
    private final ProtocolManager protocolManager;
    private final TransportPipes transportPipes;

    @Inject
    public ProtocolService(TransportPipes transportPipes) {
        protocolManager = ProtocolLibrary.getProtocolManager();
        this.transportPipes = transportPipes;
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
            spawnEntityLivingContainer.getUUIDs().write(0, uuid); // Entity UUID
            spawnEntityLivingContainer.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            spawnEntityLivingContainer.getDoubles().write(0, blockLoc.getX() + asd.getRelLoc().getDoubleX() + offset.getDoubleX()); // X Location
            spawnEntityLivingContainer.getDoubles().write(1, blockLoc.getY() + asd.getRelLoc().getDoubleY() + offset.getDoubleY()); // Y Location
            spawnEntityLivingContainer.getDoubles().write(2, blockLoc.getZ() + asd.getRelLoc().getDoubleZ() + offset.getDoubleZ()); // Z Location
            spawnEntityLivingContainer.getIntegers().write(0, asd.getEntityID()); // Entity ID
            transportPipes.getProtocolProvider().setASDYaw(spawnEntityLivingContainer, yaw); // Yaw
            
            protocolManager.sendServerPacket(player, spawnEntityLivingContainer);
            
            PacketContainer entityHeadRotationContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            entityHeadRotationContainer.getIntegers().write(0, asd.getEntityID());
            entityHeadRotationContainer.getBytes().write(0, (byte)(yaw * 256.0f / 360.0f));
            
            protocolManager.sendServerPacket(player, entityHeadRotationContainer);

            // ENTITYMETADATA
            protocolManager.sendServerPacket(player, transportPipes.getProtocolProvider().setEntityMetadata(protocolManager, asd));


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

            protocolManager.sendServerPacket(player, entityEquipmentContainer);

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
        transportPipes.getProtocolProvider().removeASD(p, armorStandData, protocolManager);
    }

}
