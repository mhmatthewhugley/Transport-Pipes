package de.robotricker.transportpipes.inventory;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.config.LangConf;
import de.robotricker.transportpipes.duct.pipe.CraftingPipe;
import de.robotricker.transportpipes.duct.pipe.filter.ItemData;
import de.robotricker.transportpipes.location.TPDirection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CraftingPipeSettingsInventory extends DuctSettingsInventory {

    @Override
    public void create() {
        inv = Bukkit.createInventory(null, 6 * 9, LangConf.Key.DUCT_INVENTORY_TITLE.get(duct.getDuctType().getFormattedTypeName()));
    }

    @Override
    public void closeForAllPlayers(TransportPipes transportPipes) {
        save(null);
        super.closeForAllPlayers(transportPipes);
    }

    @Override
    public void populate() {
        CraftingPipe pipe = (CraftingPipe) duct;
        TPDirection outputDir = pipe.getOutputDir();
        List<ItemStack> cachedItems = pipe.getCachedItems();

        ItemStack glassPane = itemService.createWildcardItem(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glassPane);
        }

        ItemStack outputDirectionItem = itemService.changeDisplayNameAndLoreConfig(new ItemStack(Material.TRIPWIRE_HOOK), LangConf.Key.DUCT_INVENTORY_CRAFTINGPIPE_OUTPUTDIRECTION.getLines(outputDir != null ? outputDir.getDisplayName() : LangConf.Key.DIRECTIONS_NONE.get()));
        inv.setItem(8, outputDirectionItem);

        for (int col = 0; col < 9; col += 4) {
            for (int row = 1; row < 4; row++) {
                inv.setItem(row * 9 + col, glassPane);
            }
        }

        for (int i = 0; i < 9; i++) {
            ItemData id = pipe.getRecipeItems()[i];
            if (id != null) {
                inv.setItem(9 + 1 + (i / 3) * 9 + i % 3, id.toItemStack().clone());
            } else {
                inv.setItem(9 + 1 + (i / 3) * 9 + i % 3, null);
            }
        }

        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                inv.setItem(9 + 5 + (i / 3) * 9 + i % 3, glassPane);
            }
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(4 * 9 + i, glassPane);
        }

        ItemStack retrieveItemsItem = itemService.changeDisplayNameAndLoreConfig(itemService.createHeadItem("5ca62fac-d094-4346-8361-e1dfdd970607", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQzNzM0NmQ4YmRhNzhkNTI1ZDE5ZjU0MGE5NWU0ZTc5ZGFlZGE3OTVjYmM1YTEzMjU2MjM2MzEyY2YifX19", null), LangConf.Key.DUCT_INVENTORY_CRAFTINGPIPE_RETRIEVECACHEDITEMS.getLines());
        inv.setItem(4 * 9 + 8, retrieveItemsItem);

        for (int i = 0; i < 9; i++) {
            inv.setItem(5 * 9 + i, cachedItems.size() > i ? cachedItems.get(i) : null);
        }

        updateResultWithDelay();

    }

    @Override
    protected boolean click(Player p, int rawSlot, ClickType ct) {
        updateResultWithDelay();

        CraftingPipe pipe = (CraftingPipe) duct;

        // clicked change output direction
        if (rawSlot == 8) {
            save(p);
            pipe.updateOutputDirection(true);
            return true;
        }

        // retrieve cached items
        if (rawSlot == 4 * 9 + 8) {
            List<ItemStack> cachedItems = new ArrayList<>(pipe.getCachedItems());
            pipe.getCachedItems().clear();
            transportPipes.runTaskSync(() -> {
                Map<Integer, ItemStack> overflow = p.getInventory().addItem(cachedItems.toArray(new ItemStack[0]));
                for (ItemStack overflowItem : overflow.values()) {
                    p.getWorld().dropItem(p.getLocation(), overflowItem);
                }
            });

            save(p);
            populate();

            return true;
        }

        // clicked on recipe items
        if (slotInRecipeGrid(rawSlot)) {
            return false;
        }

        return rawSlot < inv.getSize();
    }

    @Override
    protected boolean drag(Player p, Set<Integer> rawSlots, DragType dragType) {
        updateResultWithDelay();
        for (Integer i : rawSlots) {
            if (!(i >= inv.getSize() || slotInRecipeGrid(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean slotInRecipeGrid(int rawSlot) {
        if (rawSlot == 9 + 1 || rawSlot == 9 + 2 || rawSlot == 9 + 3) {
            return true;
        }
        if (rawSlot == 2 * 9 + 1 || rawSlot == 2 * 9 + 2 || rawSlot == 2 * 9 + 3) {
            return true;
        }
        return rawSlot == 3 * 9 + 1 || rawSlot == 3 * 9 + 2 || rawSlot == 3 * 9 + 3;
    }

    @Override
    protected boolean collect_to_cursor(Player p, ItemStack cursor, int rawSlot) {
        return cursor != null;
    }

    private void updateResultWithDelay() {
        Bukkit.getScheduler().runTask(transportPipes, () -> {
            Recipe recipe = transportPipes.getProtocolProvider().calculateRecipe(transportPipes, inv, duct);
            inv.setItem(24, recipe != null ? recipe.getResult() : null);
        });
    }

    @Override
    public void save(Player p) {

        CraftingPipe pipe = (CraftingPipe) duct;

        for (int i = 0; i < 9; i++) {
            ItemStack is = inv.getItem(9 + 1 + (i / 3) * 9 + i % 3);
            if (is != null && is.getType() != Material.AIR) {
                if (is.getAmount() > 1) {
                    ItemStack drop = is.clone();
                    drop.setAmount(is.getAmount() - 1);
                    if (p != null) {
                        p.getWorld().dropItem(p.getLocation(), drop);
                    } else {
                        duct.getWorld().dropItem(duct.getBlockLoc().toLocation(duct.getWorld()), drop);
                    }
                    is.setAmount(1);
                }
                pipe.getRecipeItems()[i] = new ItemData(is);
            } else {
                pipe.getRecipeItems()[i] = null;
            }
        }

        pipe.setRecipe(transportPipes.getProtocolProvider().calculateRecipe(transportPipes, inv, duct));

    }
}
