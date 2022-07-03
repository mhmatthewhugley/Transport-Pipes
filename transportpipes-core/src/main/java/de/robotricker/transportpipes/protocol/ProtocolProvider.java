package de.robotricker.transportpipes.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;

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

}
