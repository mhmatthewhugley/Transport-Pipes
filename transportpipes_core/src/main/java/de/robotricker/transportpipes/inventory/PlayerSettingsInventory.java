package de.robotricker.transportpipes.inventory;

import de.robotricker.transportpipes.PlayerSettingsService;
import de.robotricker.transportpipes.ResourcepackService;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.config.LangConf;
import de.robotricker.transportpipes.config.PlayerSettingsConf;
import de.robotricker.transportpipes.duct.DuctRegister;
import de.robotricker.transportpipes.items.ItemService;
import de.robotricker.transportpipes.rendersystems.ModelledRenderSystem;
import de.robotricker.transportpipes.rendersystems.RenderSystem;
import de.robotricker.transportpipes.rendersystems.VanillaRenderSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PlayerSettingsInventory extends IndividualInventory implements Listener {

    @Inject
    private ItemService itemService;
    @Inject
    private PlayerSettingsService playerSettingsService;
    @Inject
    private DuctRegister ductRegister;
    @Inject
    private ResourcepackService resourcepackService;
    @Inject
    private TransportPipes transportPipes;

    private final Set<Inventory> inventories;

    public PlayerSettingsInventory() {
        inventories = new HashSet<>();
    }

    @Override
    Inventory create(Player p) {
        Inventory inv = Bukkit.createInventory(null, 2 * 9, LangConf.Key.PLAYER_SETTINGS_TITLE.get());
        inventories.add(inv);

        PlayerSettingsConf playerSettingsConf = playerSettingsService.getOrCreateSettingsConf(p);

        ItemStack decreaseBtn = new ItemStack(Material.SUNFLOWER);
        itemService.changeDisplayName(decreaseBtn, LangConf.Key.PLAYER_SETTINGS_DECREASE_RENDERDISTANCE.get());
        ItemStack increaseBtn = new ItemStack(Material.SUNFLOWER);
        itemService.changeDisplayName(increaseBtn, LangConf.Key.PLAYER_SETTINGS_INCREASE_RENDERDISTANCE.get());

        ItemStack eye = new ItemStack(Material.ENDER_EYE, playerSettingsConf.getRenderDistance());
        itemService.changeDisplayNameAndLore(eye, LangConf.Key.PLAYER_SETTINGS_RENDERDISTANCE.get(playerSettingsConf.getRenderDistance()));

        ItemStack glassPane = itemService.createWildcardItem(Material.GRAY_STAINED_GLASS_PANE);

        itemService.populateInventoryLine(inv, 0, glassPane, glassPane, decreaseBtn, glassPane, eye, glassPane, increaseBtn, glassPane, glassPane);

        String renderSystemName = playerSettingsConf.getRenderSystemName();

        ItemStack renderSystemRepresentationItem = RenderSystem.getItem(renderSystemName, itemService, ductRegister);
        itemService.changeDisplayNameAndLore(Objects.requireNonNull(renderSystemRepresentationItem), LangConf.Key.PLAYER_SETTINGS_RENDERSYSTEM.get(RenderSystem.getLocalizedRenderSystemName(renderSystemName)));

        boolean showItems = playerSettingsConf.isShowItems();
        ItemStack itemVisibilityItem = showItems ? itemService.changeDisplayNameAndLore(new ItemStack(Material.GLASS), LangConf.Key.PLAYER_SETTINGS_ITEM_VISIBILITY_SHOW.get()) : itemService.changeDisplayNameAndLore(new ItemStack(Material.BARRIER), LangConf.Key.PLAYER_SETTINGS_ITEM_VISIBILITY_HIDE.get());

        itemService.populateInventoryLine(inv, 1, glassPane, glassPane, glassPane, renderSystemRepresentationItem, glassPane, itemVisibilityItem, glassPane, glassPane, glassPane);

        return inv;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (inventories.contains(e.getInventory()) && e.getWhoClicked() instanceof Player player) {
            if (itemService.isItemWildcardOrBarrier(e.getCurrentItem())) {
                e.setCancelled(true);
                return;
            }

            PlayerSettingsConf playerSettingsConf = playerSettingsService.getOrCreateSettingsConf(player);

            e.setCancelled(true);

            if (e.getRawSlot() == 2) {
                // decrease render distance
                int before = playerSettingsConf.getRenderDistance();
                int after = before - 1;
                if (after >= 1) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    playerSettingsConf.setRenderDistance(after);
                    openInv(player);
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
                }
            } else if (e.getRawSlot() == 6) {
                // increase render distance
                int before = playerSettingsConf.getRenderDistance();
                int after = before + 1;
                if (after <= 64) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    playerSettingsConf.setRenderDistance(after);
                    openInv(player);
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);

                }
            } else if (e.getRawSlot() == 12) {

                if (resourcepackService.getResourcepackMode() == ResourcepackService.ResourcepackMode.NONE) {
                    LangConf.Key.RENDERSYSTEM_BLOCK.sendMessage((Player) e.getWhoClicked());
                    return;
                }

                // change render system
                String oldRenderSystemName = playerSettingsConf.getRenderSystemName();
                String newRenderSystemName = null;
                if (oldRenderSystemName.equalsIgnoreCase(VanillaRenderSystem.getDisplayName())) {
                    if(resourcepackService.getResourcepackMode() == ResourcepackService.ResourcepackMode.DEFAULT && !resourcepackService.getResourcepackPlayers().contains((Player) e.getWhoClicked())) {
                        player.closeInventory();
                        resourcepackService.loadResourcepackForPlayer((Player) e.getWhoClicked());
                        return;
                    }
                    newRenderSystemName = ModelledRenderSystem.getDisplayName();
                } else if (oldRenderSystemName.equalsIgnoreCase(ModelledRenderSystem.getDisplayName())) {
                    newRenderSystemName = VanillaRenderSystem.getDisplayName();
                }

                transportPipes.changeRenderSystem((Player) e.getWhoClicked(), newRenderSystemName);

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                openInv(player);
            } else if (e.getRawSlot() == 14) {
                // change item visibility
                boolean showItems = playerSettingsConf.isShowItems();
                playerSettingsConf.setShowItems(!showItems);

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                openInv(player);
            }

        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if (inventories.contains(e.getInventory()) && e.getPlayer() instanceof Player) {
            inventories.remove(e.getInventory());
        }
    }

}
