package de.robotricker.transportpipes.utils;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.api.DuctBreakEvent;
import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.location.BlockLocation;
import org.bukkit.Bukkit;

import javax.inject.Inject;

public class WorldEditUtils {

    @Inject
    private TransportPipes plugin;
    @Inject
    private GlobalDuctManager globalDuctManager;

    @Subscribe
    public void onPipeWorldEdit(EditSessionEvent event) {
        World world = event.getWorld();
        if (world == null) {
            return;
        }

        event.setExtent(new AbstractDelegateExtent(event.getExtent()) {
            @Override
            public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 pos, T block) throws WorldEditException {

                Duct duct = globalDuctManager.getDuctAtLoc(BukkitAdapter.adapt(world), new BlockLocation(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()));

                if (duct != null) {

                    BlockMaterial before = getExtent().getBlock(pos).getBlockType().getMaterial();
                    BlockMaterial after = block.getBlockType().getMaterial();

                    if (after.isAir() || (after != before)) {
                        BlockLocation ductLoc = duct.getBlockLoc();
                        globalDuctManager.unregisterDuct(duct);
                        globalDuctManager.unregisterDuctInRenderSystem(duct, true);
                        globalDuctManager.updateNeighborDuctsConnections(duct);
                        globalDuctManager.updateNeighborDuctsInRenderSystems(duct, true);
                        globalDuctManager.playDuctDestroyActions(duct, null);

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            DuctBreakEvent ductBreakEvent = new DuctBreakEvent(null, ductLoc);
                            Bukkit.getPluginManager().callEvent(ductBreakEvent);
                        });
                    }
                }

                return getExtent().setBlock(pos, block);
            }
        });
    }
}
