package de.robotricker.transportpipes.saving;

import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.DuctRegister;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.duct.types.DuctType;
import de.robotricker.transportpipes.items.ItemService;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.TPDirection;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.World;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuctLoader {

    @Inject
    private GlobalDuctManager globalDuctManager;
    @Inject
    private DuctRegister ductRegister;
    @Inject
    private ItemService itemService;

    public void loadDuctsSync(World world, CompoundTag compoundTag) {
        ListTag<CompoundTag> listTag = compoundTag.getListTag("ducts").asCompoundTagList();

        Map<Duct, CompoundTag> ductCompoundTagMap = new HashMap<>();
        for (CompoundTag ductTag : listTag) {
            DuctType ductType = ductRegister.loadDuctTypeFromNBTTag(ductTag);
            BlockLocation blockLoc = ductRegister.loadBlockLocFromNBTTag(ductTag);
            List<TPDirection> blockedConnections = ductRegister.loadBlockedConnectionsFromNBTTag(ductTag); 
            if (ductType == null || blockLoc == null) {
                continue;
            }
            Duct duct = globalDuctManager.createDuctObject(ductType, blockLoc, world, blockLoc.toLocation(world).getChunk());
            if (blockedConnections != null) duct.getBlockedConnections().addAll(blockedConnections);
            globalDuctManager.registerDuct(duct);
            ductCompoundTagMap.put(duct, ductTag);
        }
        // load duct specific nbt stuff later in order to be able to access other ducts inside this load process
        for (Duct duct : ductCompoundTagMap.keySet()) {
            globalDuctManager.updateDuctConnections(duct);
            duct.loadFromNBTTag(ductCompoundTagMap.get(duct), itemService);
            globalDuctManager.registerDuctInRenderSystems(duct, false);
        }
    }

}
