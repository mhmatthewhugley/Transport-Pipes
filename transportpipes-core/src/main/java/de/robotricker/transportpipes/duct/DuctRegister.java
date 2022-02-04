package de.robotricker.transportpipes.duct;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.config.GeneralConf;
import de.robotricker.transportpipes.duct.factory.DuctFactory;
import de.robotricker.transportpipes.duct.manager.DuctManager;
import de.robotricker.transportpipes.duct.types.BaseDuctType;
import de.robotricker.transportpipes.duct.types.DuctType;
import de.robotricker.transportpipes.items.ItemManager;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.TPDirection;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class DuctRegister {

    private final List<BaseDuctType<? extends Duct>> baseDuctTypes;

    private final TransportPipes plugin;
    private final GeneralConf generalConf;

    @Inject
    public DuctRegister(TransportPipes plugin, GeneralConf generalConf) {
        this.plugin = plugin;
        this.generalConf = generalConf;
        this.baseDuctTypes = new ArrayList<>();
    }

    public <T extends Duct> BaseDuctType<T> registerBaseDuctType(String name, Class<? extends DuctManager<T>> ductManagerClass, Class<? extends DuctFactory<T>> ductFactoryClass, Class<? extends ItemManager<T>> itemManagerClass) {
        if (baseDuctTypes.stream().anyMatch(bdt -> bdt.getName().equalsIgnoreCase(name))) {
            throw new IllegalArgumentException("BaseDuctType '" + name + "' already exists");
        }
        BaseDuctType<T> baseDuctType = new BaseDuctType<>(name, plugin.getInjector().newInstance(ductManagerClass), plugin.getInjector().newInstance(ductFactoryClass), plugin.getInjector().newInstance(itemManagerClass));
        this.baseDuctTypes.add(baseDuctType);
        baseDuctType.getDuctManager().registerDuctTypes();
        baseDuctType.getItemManager().registerItems();

        if (generalConf.isCraftingEnabled()) {
            baseDuctType.getDuctManager().registerRecipes();
        }

        return baseDuctType;
    }

    public List<BaseDuctType<? extends Duct>> baseDuctTypes() {
        return baseDuctTypes;
    }

    @SuppressWarnings("unchecked")
    public <T extends Duct> BaseDuctType<T> baseDuctTypeOf(String displayName) {
        return (BaseDuctType<T>) baseDuctTypes().stream().filter(bdt -> bdt.getName().equalsIgnoreCase(displayName)).findAny().orElse(null);
    }

    public void saveDuctTypeToNBTTag(DuctType ductType, CompoundTag ductTag) {
        ductTag.putString("baseDuctType", ductType.getBaseDuctType().getName());
        ductTag.putString("ductType", ductType.getName());
    }

    public void saveBlockLocToNBTTag(BlockLocation blockLoc, CompoundTag ductTag) {
        ductTag.putString("blockLoc", blockLoc.toString());
    }
    
    public void saveDuctBlockedConnectionsToNBTTag(List<TPDirection> blockedConnections, CompoundTag ductTag) {
        ListTag<StringTag> blockedConnectionsListTag = new ListTag<>(StringTag.class);
        for (TPDirection direction : blockedConnections) {
            blockedConnectionsListTag.addString(direction.toString());
        }
        ductTag.put("blockedConnections", blockedConnectionsListTag);
    }

    public DuctType loadDuctTypeFromNBTTag(CompoundTag ductTag) {
        BaseDuctType<? extends Duct> bdt = baseDuctTypeOf(ductTag.getString("baseDuctType"));
        if (bdt == null) {
            return null;
        }
        return bdt.ductTypeOf(ductTag.getString("ductType"));
    }

    public BlockLocation loadBlockLocFromNBTTag(CompoundTag ductTag) {
        return BlockLocation.fromString(ductTag.getString("blockLoc"));
    }
    
    public List<TPDirection> loadBlockedConnectionsFromNBTTag(CompoundTag ductTag) {
        List<TPDirection> blockedConnectionsList = new ArrayList<>();
        ListTag<?> listTag = ductTag.getListTag("blockedConnections");
        if (listTag == null) {
            return null;
        }
        ListTag<StringTag> blockedConnectionsListTag = listTag.asStringTagList();
        for (StringTag directionStringTag : blockedConnectionsListTag) {
            blockedConnectionsList.add(TPDirection.valueOf(directionStringTag.getValue()));
        }
        return blockedConnectionsList;
    }

}
