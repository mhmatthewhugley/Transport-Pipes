package de.robotricker.transportpipes.protocol;

import de.robotricker.transportpipes.location.RelativeLocation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

public class ArmorStandData implements Cloneable {

    private final RelativeLocation relLoc;
    private final boolean small;
    private final Vector direction;
    private final Vector headRotation;
    private final Vector armRotation;
    private final ItemStack headItem;
    private final ItemStack handItem;
    private int entityID = -1;

    public ArmorStandData(RelativeLocation relLoc, boolean small, Vector direction, Vector headRotation, Vector armRotation, ItemStack headItem, ItemStack handItem) {
        this.relLoc = relLoc;
        this.small = small;
        this.direction = direction;
        this.headRotation = headRotation;
        this.armRotation = armRotation;
        this.headItem = headItem;
        this.handItem = handItem;
    }

    public RelativeLocation getRelLoc() {
        return relLoc;
    }

    public boolean isSmall() {
        return small;
    }

    public Vector getDirection() {
        return direction;
    }

    public Vector getHeadRotation() {
        return headRotation;
    }

    public Vector getArmRotation() {
        return armRotation;
    }

    public ItemStack getHeadItem() {
        return headItem;
    }

    public ItemStack getHandItem() {
        return handItem;
    }

    public int getEntityID() {
        return entityID;
    }

    public void setEntityID(int entityID) {
        this.entityID = entityID;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ArmorStandData clone() {
        return new ArmorStandData(relLoc, small, direction, headRotation, armRotation, headItem, handItem);
    }

    public boolean isSimilar(ArmorStandData armorStandData) {
        if (armorStandData == null) {
            return false;
        }
        return Objects.equals(relLoc, armorStandData.relLoc) &&
                small == armorStandData.small &&
                Objects.equals(direction, armorStandData.direction) &&
                Objects.equals(headRotation, armorStandData.headRotation) &&
                Objects.equals(armRotation, armorStandData.armRotation) &&
                Objects.equals(headItem, armorStandData.headItem) &&
                Objects.equals(handItem, armorStandData.handItem);
    }

    public boolean isSimilarPos(ArmorStandData armorStandData) {
        if (armorStandData == null) {
            return false;
        }
        return Objects.equals(relLoc, armorStandData.relLoc);
    }

}
