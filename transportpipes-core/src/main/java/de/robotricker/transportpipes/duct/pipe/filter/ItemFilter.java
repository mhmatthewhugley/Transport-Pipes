package de.robotricker.transportpipes.duct.pipe.filter;

import de.robotricker.transportpipes.items.ItemService;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemFilter {

    public static final int MAX_ITEMS_PER_ROW = 32;

    private final ItemData[] filterItems;
    private FilterMode filterMode;
    private FilterStrictness filterStrictness;

    public ItemFilter() {
        filterItems = new ItemData[MAX_ITEMS_PER_ROW];
        filterMode = FilterMode.NORMAL;
        filterStrictness = FilterStrictness.MATERIAL_METADATA;
    }

    public ItemData[] getFilterItems() {
        return filterItems;
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    public FilterStrictness getFilterStrictness() {
        return filterStrictness;
    }

    public void setFilterStrictness(FilterStrictness filterStrictness) {
        this.filterStrictness = filterStrictness;
    }

    public FilterResponse applyFilter(ItemStack item) {
        if (item == null || getFilterMode() == FilterMode.BLOCK_ALL) {
        	return new FilterResponse(0, false);
        }
        if (getFilterMode() == FilterMode.NORMAL) {
            int weight = 0;
            boolean hasItems = false;
            for (ItemData id : filterItems) {
            	if (id != null) {
            		hasItems = true;
                    if (matchesItemStrictness(id.toItemStack(), item)) {
                        weight++;
                    }
            	}
            }
            return hasItems ? new FilterResponse(weight, weight > 0) : new FilterResponse(1, true);
        }
        if (getFilterMode() == FilterMode.INVERTED) {
            for (ItemData id : filterItems) {
                if (id != null) {
                	if (matchesItemStrictness(id.toItemStack(), item)) {
                		return new FilterResponse(0, false);
                	}
                }
            }
            return new FilterResponse(1, true);
        }
        return new FilterResponse(0, false);
    }

    private boolean matchesItemStrictness(ItemStack mask, ItemStack itemStack) {
        return switch (getFilterStrictness()) {
            case MATERIAL -> mask.getType() == itemStack.getType();
            case MATERIAL_METADATA -> mask.getType() == itemStack.getType() && Bukkit.getItemFactory().equals(mask.getItemMeta(), itemStack.getItemMeta());
        };
    }

    public List<ItemStack> getAsItemStacks() {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < MAX_ITEMS_PER_ROW; i++) {
            if (filterItems[i] != null) {
                itemStacks.add(filterItems[i].toItemStack());
            }
        }
        return itemStacks;
    }

    public void saveToNBTTag(CompoundTag compoundTag, ItemService itemService) {
        compoundTag.putInt("filterMode", filterMode.ordinal());
        compoundTag.putInt("filterStrictness", filterStrictness.ordinal());
        ListTag<StringTag> itemDataListTag = new ListTag<>(StringTag.class);
        for (int i = 0; i < MAX_ITEMS_PER_ROW; i++) {
            ItemData itemData = filterItems[i];
            if (itemData == null) {
                itemDataListTag.add(new StringTag());
            } else {
                itemDataListTag.addString(itemService.serializeItemStack(itemData.toItemStack()));
            }
        }
        compoundTag.put("filterItems", itemDataListTag);
    }

    public void loadFromNBTTag(CompoundTag compoundTag, ItemService itemService) {
        filterMode = FilterMode.values()[compoundTag.getInt("filterMode")];
        filterStrictness = FilterStrictness.values()[compoundTag.getInt("filterStrictness")];
        ListTag<StringTag> itemDataListTag = compoundTag.getListTag("filterItems").asStringTagList();
        for (int i = 0; i < MAX_ITEMS_PER_ROW; i++) {
            if(i >= itemDataListTag.size()) {
                filterItems[i] = null;
                continue;
            }
            ItemStack deserialized = itemService.deserializeItemStack(itemDataListTag.get(i).getValue());
            filterItems[i] = deserialized != null ? new ItemData(deserialized) : null;
        }
    }

}
