package de.robotricker.transportpipes.saving;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.DuctRegister;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.items.ItemService;
import de.robotricker.transportpipes.location.BlockLocation;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.World;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentSkipListMap;

public class DuctSaver {

    @Inject
    private GlobalDuctManager globalDuctManager;
    @Inject
    private DuctRegister ductRegister;
    @Inject
    private TransportPipes transportPipes;
    @Inject
    private ItemService itemService;

    public void saveDuctsSync(World world) {
        ListTag<CompoundTag> listTag = new ListTag<>(CompoundTag.class);
        ConcurrentSkipListMap<BlockLocation, Duct> ducts = globalDuctManager.getDucts(world);
        for (BlockLocation bl : ducts.keySet()) {
            Duct duct = ducts.get(bl);
            CompoundTag ductTag = new CompoundTag();

            ductRegister.saveDuctTypeToNBTTag(duct.getDuctType(), ductTag);
            ductRegister.saveBlockLocToNBTTag(duct.getBlockLoc(), ductTag);
            ductRegister.saveDuctBlockedConnectionsToNBTTag(duct.getBlockedConnections(), ductTag);
            duct.saveToNBTTag(ductTag, itemService);

            listTag.add(ductTag);
        }
        if (listTag.size() == 0) {
            return;
        }

        try {

            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("ducts", listTag);
            compoundTag.putString("version", transportPipes.getDescription().getVersion());

            NBTUtil.write(compoundTag, Paths.get(world.getWorldFolder().getAbsolutePath(), "ducts.dat").toFile(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
