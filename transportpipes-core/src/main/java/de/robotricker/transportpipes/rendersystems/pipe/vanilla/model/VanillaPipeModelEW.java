package de.robotricker.transportpipes.rendersystems.pipe.vanilla.model;

import de.robotricker.transportpipes.hitbox.AxisAlignedBB;
import de.robotricker.transportpipes.location.RelativeLocation;
import de.robotricker.transportpipes.protocol.ArmorStandData;
import de.robotricker.transportpipes.rendersystems.pipe.vanilla.model.data.VanillaPipeModelData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class VanillaPipeModelEW extends VanillaPipeModel {

    public VanillaPipeModelEW() {
        super();
        aabb = new AxisAlignedBB(0, 0.22, 0.22, 1, 0.78, 0.78);
    }

    @Override
    public List<ArmorStandData> createASD(VanillaPipeModelData data) {
        return createSimplePipeASD(pipeBlocks.get(data.getPipeType()));
    }

    private List<ArmorStandData> createSimplePipeASD(ItemStack block) {
        List<ArmorStandData> asd = new ArrayList<>();

        asd.add(new ArmorStandData(new RelativeLocation(0.05f, -0.33f, 0.0f), false, new Vector(1, 0, 0), new Vector(0f, 0f, 0f), new Vector(-10f, 0f, 45f), null, ITEM_BLAZE_ROD)); // Top Left
        asd.add(new ArmorStandData(new RelativeLocation(0.05f, -1.09f, 0.5f - 0.89f), false, new Vector(1, 0, 0), new Vector(0f, 0f, 0f), new Vector(-10f, 0f, 135f), null, ITEM_BLAZE_ROD)); // Top Right
        asd.add(new ArmorStandData(new RelativeLocation(0.05f, -1.09f - 0.47f, 0.5f - 0.41f), false, new Vector(1, 0, 0), new Vector(0f, 0f, 0f), new Vector(-10f, 0f, 135f), null, ITEM_BLAZE_ROD)); // Bottom Left
        asd.add(new ArmorStandData(new RelativeLocation(0.05f, -0.33f - 0.47f, 0.5f - 0.99f), false, new Vector(1, 0, 0), new Vector(0f, 0f, 0f), new Vector(-10f, 0f, 45f), null, ITEM_BLAZE_ROD)); // Bottom Right
        asd.add(new ArmorStandData(new RelativeLocation(0.55f - 0.3f, -0.43f, 0.5f), true, new Vector(1, 0, 0), new Vector(0f, 0f, 0f), new Vector(0f, 0f, 0f), block, null));
        asd.add(new ArmorStandData(new RelativeLocation(0.55f + 0.2f, -0.43f, 0.5f), true, new Vector(1, 0, 0), new Vector(0f, 0f, 0f), new Vector(0f, 0f, 0f), block, null));

        return asd;
    }

}
