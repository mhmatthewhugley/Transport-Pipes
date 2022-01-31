package de.robotricker.transportpipes.protocol;

import com.comphenix.protocol.ProtocolManager;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;

import java.util.List;

public abstract class ProtocolProvider {

    // Armor stand packet indexes
    final public int asMask;
    final public int headRot;
    final public int rArmRot;

    public ProtocolProvider(int asMask, int headRot, int rArmRot) {
        this.asMask = asMask;
        this.headRot = headRot;
        this.rArmRot = rArmRot;
    }

    // Armor stand entity destroy packet
    public abstract void removeASD(Player p, List<ArmorStandData> armorStandData, ProtocolManager protocolManager);

    // Crafting pipe recipe calculation
    public abstract Recipe calculateRecipe(TransportPipes transportPipes, Inventory inventory, Duct duct);

}
